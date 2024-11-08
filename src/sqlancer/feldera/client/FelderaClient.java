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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FelderaClient {
    private final String url;
    private final String pipeline;
    private final CloseableHttpClient client;

    private String ddl = "";

    private static String url(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }

        return url + "v0/";
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
            throw new FelderaException("error making put request to create an empty pipeline: " + e);
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
            throw new FelderaException("error making put request to create an empty pipeline: " + e);
        }
    }

    public FelderaClient(String url, String pipeline) throws FelderaException {
        this.url = url(url);
        this.pipeline = pipeline;
        this.client = HttpClientBuilder.create().build();

        this.createEmptyPipeline();
    }

    public void executeQuery(String query) throws FelderaException {
        this.waitForCompilation();
        this.start();
        this.executeAdhocQuery(query);
    }

    public void changePipelineState(String state) throws FelderaException {
        HttpPost httpPost = new HttpPost(this.pipelineUrl(state));
        int status;

        try (CloseableHttpResponse resp = client.execute(httpPost)) {
            status = resp.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new FelderaException("error: failed to change pipeline state: " + e);
        }

        if (status == 404) {
            this.createEmptyPipeline();
            this.changePipelineState(state);
            return;
        }

        if (status < 200 || status > 299) {
            throw new FelderaException("error: got status " + status + " back from feldera, expected 2xx");
        }
    }

    private void blockTillDesiredState(String desired) throws FelderaException {
        while (true) {
            Map<String, Object> map = this.get();
            if (map == null) {
                throw new FelderaException("error: got empty response from feldera");
            }
            String deployment_status = (String) map.get("deployment_status");
            if (deployment_status.equalsIgnoreCase(desired)) {
                break;
            }
        }
    }

    public void start() throws FelderaException {
        changePipelineState("start");
        blockTillDesiredState("running");
    }

    @SuppressWarnings("unused")
    public void restart() throws FelderaException {
        this.shutdown();
        this.start();
    }

    @SuppressWarnings("unused")
    public void pause() throws FelderaException {
        changePipelineState("pause");
        blockTillDesiredState("paused");
    }

    @SuppressWarnings("unused")
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
            throw new FelderaException("error: failed to build HttpGet request to feldera: " + e);
        }
    }

    private void executeAdhocQuery(String query) throws FelderaException {
        Map<String, String> params = new HashMap<>();
        params.put("pipeline_name", this.pipelineName());
        params.put("sql", query);
        params.put("format", "text"); // TODO: change to json

        HttpGet httpGet = createHttpGet(this.pipelineUrl("query"), params);
        try {
            CloseableHttpResponse resp = this.client.execute(httpGet);
            resp.close();
        } catch (IOException e) {
            throw new FelderaException("error: failed to execute adhoc query: " + e);
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

            if (status != 200) {
                throw new FelderaException(status);
            }

            String json = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);

            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new FelderaException("error: failed to get pipeline details: " + e);
        }
    }

    public void waitForCompilation() throws FelderaException {
        while (true) {
            Map<String, Object> map = this.get();
            if (map == null) {
                throw new AssertionError("error: pipeline not created yet, cant wait for compilation");
            }

            String program_status = (String) map.get("program_status");
            if (Objects.equals(program_status, "Success")) {
                break;
            }

            if (!Objects.equals(program_status, "Pending") && !Objects.equals(program_status, "CompilingSql") && !Objects.equals(program_status, "CompilingRust")) {
                throw new FelderaException("error: failed to compile program");
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
