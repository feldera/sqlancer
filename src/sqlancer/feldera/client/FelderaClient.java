package sqlancer.feldera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FelderaClient {
    private final String url;
    private final String pipeline;
    private final CloseableHttpClient client;
    private final ArrayList<String> inserts = new ArrayList<>();

    private String ddl = "";

    private static String url(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }

        return url + "v0/";
    }

    private FelderaException makeException(String error) {
        return new FelderaException(
                error + "\nPipeline: " + this.pipeline + "\nInserts: " + String.join("\n", inserts));
    }

    private String pipelineUrl() {
        return this.url + "pipelines/" + this.pipeline;
    }

    private String pipelineUrl(String suffix) {
        return this.pipelineUrl() + "/" + suffix;
    }

    private void createEmptyPipeline() throws FelderaException {
        HttpPut httpPut = new HttpPut(this.pipelineUrl());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("name", pipelineName());
        node.put("description", "sqlancer test");
        node.put("program_code", "");
        node.putObject("runtime_config");
        node.putObject("program_config");
        String json = node.toString();
        httpPut.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try {
            this.client.execute(httpPut);
        } catch (IOException e) {
            throw makeException("error making put request to create an empty pipeline: " + e);
        }
    }

    private void updatePipeline() throws FelderaException {
        HttpPatch httpPatch = new HttpPatch(this.pipelineUrl());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("program_code", this.ddl);
        httpPatch.setEntity(new StringEntity(node.toString(), ContentType.APPLICATION_JSON));

        try {
            CloseableHttpResponse conn = this.client.execute(httpPatch);
            conn.close();
        } catch (IOException e) {
            throw makeException("error making put request to create an empty pipeline: " + e);
        }
    }

    public FelderaClient(String url, String pipeline) throws FelderaException {
        this.url = url(url);
        this.pipeline = pipeline;
        this.client = HttpClientBuilder.create().build();

        this.createEmptyPipeline();
    }

    public void executeQuery(String query) throws FelderaException {
        this.inserts.add(query);
        this.waitForCompilation();
        this.start();
        this.executeAdhocQuery(query);
    }

    public void changePipelineState(String state) throws FelderaException {
        Map<String, Object> get = this.get();
        assert get != null : makeException("err: cannot change status, pipeline not found: " + this.pipeline);

        String deployment_status = (String) get.get("deployment_status");
        if (deployment_status.equalsIgnoreCase(state)) {
            return;
        } else if (deployment_status.equalsIgnoreCase("Failed")) {
            throw makeException("err: pipeline '" + this.pipeline + "' is in failed state");
        }

        HttpPost httpPost = new HttpPost(this.pipelineUrl(state));
        int status;
        String json;

        try (CloseableHttpResponse resp = client.execute(httpPost)) {
            status = resp.getStatusLine().getStatusCode();
            json = EntityUtils.toString(resp.getEntity());
        } catch (IOException e) {
            throw makeException("error: failed to change pipeline state: " + e);
        }

        if (status < 200 || status > 299) {
            throw makeException("error: failed to change pipeline state: got response:\n" + json);
        }
    }

    public void blockTillDesiredState(String desired) throws FelderaException {
        while (true) {
            Map<String, Object> map = this.get();
            if (map == null) {
                throw makeException("error: got empty response from feldera");
            }
            String deployment_status = (String) map.get("deployment_status");
            if (deployment_status.equalsIgnoreCase(desired)) {
                break;
            }
        }
    }

    public void start() throws FelderaException {
        // HACK: weirdly sometimes we call /start before things compile sometimes
        waitForCompilation();
        changePipelineState("start");
        blockTillDesiredState("running");
    }

    public void pause() throws FelderaException {
        changePipelineState("pause");
        blockTillDesiredState("paused");
    }

    public void shutdown() throws FelderaException {
        changePipelineState("shutdown");
        blockTillDesiredState("shutdown");
    }

    private HttpGet createHttpGet(String url, Map<String, String> params) throws FelderaException {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            URI uri = uriBuilder.build();

            return new HttpGet(uri);
        } catch (Exception e) {
            throw makeException("error: failed to build HttpGet request to feldera: " + e);
        }
    }

    public HashMap<String, Object> executeSelect(String select) throws FelderaException {
        this.pause();
        return this.executeAdhocQuery(select);
    }

    private HashMap<String, Object> executeAdhocQuery(String query) throws FelderaException {
        Map<String, String> params = new HashMap<>();
        params.put("pipeline_name", this.pipelineName());
        params.put("sql", query);
        params.put("format", "json");

        HttpGet httpGet = createHttpGet(this.pipelineUrl("query"), params);
        try {
            CloseableHttpResponse resp = this.client.execute(httpGet);

            String json = EntityUtils.toString(resp.getEntity());
            if (json.isBlank()) {
                return new HashMap<>();
            }
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw makeException("error: failed to execute adhoc query: " + e);
        }
    }

    @Nullable
    public HashMap<String, Object> get() throws FelderaException {
        HttpGet httpGet = new HttpGet(this.pipelineUrl());

        try (CloseableHttpResponse resp = this.client.execute(httpGet)) {
            int status = resp.getStatusLine().getStatusCode();

            if (status == 404) {
                return null;
            }

            String json = EntityUtils.toString(resp.getEntity());

            if (status != 200) {
                throw makeException("err: got invalid response from feldera: " + status + "\nresp:\n" + json);
            }

            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);

            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw makeException("error: failed to get pipeline details: " + e);
        }
    }

    public void waitForCompilation() throws FelderaException {
        while (true) {
            Map<String, Object> map = this.get();
            if (map == null) {
                throw new AssertionError("error: pipeline not created yet, cant wait for compilation");
            }

            Object program_status = map.get("program_status");
            if (program_status instanceof String) {
                if (Objects.equals(program_status, "Success")) {
                    break;
                }
            } else if (program_status instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> err_map = (Map<String, Object>) program_status;
                if (err_map.containsKey("SqlError")) {
                    throw makeException("compilation error: " + err_map.get("SqlError").toString());
                } else if (err_map.containsKey("RustError")) {
                    throw makeException("compilation error: " + err_map.get("RustError").toString());
                }
            }

            String deployment_status = (String) map.get("deployment_status");
            if (Objects.equals(deployment_status, "Failed")) {
                throw makeException("failed to deploy pipeline:\n" + map);
            }

            if (!Objects.equals(program_status, "Pending") && !Objects.equals(program_status, "CompilingSql")
                    && !Objects.equals(program_status, "CompilingRust")) {
                throw makeException("error: failed to compile program:\n" + map);
            }
        }
    }

    public void buffer(String query) throws FelderaException {
        if (query.startsWith("INSERT")) {
            this.executeQuery(query);
        } else {
            this.ddl += query;
            this.updatePipeline();
        }
    }

    public String pipelineName() {
        return this.pipeline;
    }
}
