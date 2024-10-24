package sqlancer.feldera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FelderaClient {
    private final String url;
    private final String pipeline;
    private final CloseableHttpClient client;

    @SuppressWarnings("unused")
    private String ddl = "";

    @SuppressWarnings("unused")
    private List<String> inserts = new ArrayList<>();

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

    public FelderaClient(String url, String pipeline) {
        this.url = url(url);
        this.pipeline = pipeline;

        this.client = HttpClientBuilder.create().build();
    }

    public void executeQuery(String query) throws Exception {
        // TODO
        this.changePipelineState(query);
    }

    public void changePipelineState(String state) throws Exception {
        HttpPost httpPost = new HttpPost(this.pipelineUrl(state));

        try (CloseableHttpResponse resp = client.execute(httpPost)) {
            int status = resp.getStatusLine().getStatusCode();
            if (status != 200 && status != 404) {
                throw new FelderaException(status);
            }
        }
    }

    public void start() throws Exception {
        changePipelineState("start");
    }

    @SuppressWarnings("unused")
    public void restart() throws Exception {
        changePipelineState("shutdown");
        changePipelineState("start");
    }

    @SuppressWarnings("unused")
    public void pause() throws Exception {
        changePipelineState("pause");
    }

    @SuppressWarnings("unused")
    public void shutdown() throws Exception {
        changePipelineState("shutdown");
    }

    @Nullable
    public HashMap<String, Object> get() throws Exception {
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
        }
    }

    public void buffer(String query) {
        String first_word = query.substring(query.indexOf(" "));
        if (first_word.equalsIgnoreCase("INSERT")) {
            this.inserts.add(query);
        } else {
            this.ddl += query;
        }
    }

    public String pipelineName() {
        return this.pipeline;
    }
}
