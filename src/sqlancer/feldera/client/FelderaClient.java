package sqlancer.feldera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;

public class FelderaClient {
    @SuppressWarnings("unused")
    private String key;
    private final String url;
    private final String pipeline;
    private final CloseableHttpClient client;

    @SuppressWarnings("unused")
    private String ddl;

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

    private String authKey() {
        return "Bearer " + key;
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
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, this.authKey());

        try (CloseableHttpResponse resp = client.execute(httpPost)) {
            int status = resp.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("feldera: got unexpected response with status: " + status);
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

    public HashMap<String, Object> get() throws Exception {
        HttpGet httpGet = new HttpGet(this.pipelineUrl());
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, this.authKey());

        try (CloseableHttpResponse resp = this.client.execute(httpGet)) {
            resp.wait();
            int status = resp.getStatusLine().getStatusCode();

            if (status == 404) {
                return new HashMap<>();
            }

            if (status != 200) {
                throw new RuntimeException("feldera: got unepexcted response with status " + status);
            }

            String json = EntityUtils.toString(resp.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);

            return mapper.readValue(json, mapType);
        }
    }
}
