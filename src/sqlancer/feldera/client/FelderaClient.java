package sqlancer.feldera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FelderaClient {
    private final HttpRequests httpRequests;

    public FelderaClient(String url) {
        this.httpRequests = new HttpRequests(url + "/v0");
    }

    public FelderaPipeline getPipeline(String name) throws Exception {
        String resp = this.httpRequests.get(String.format("/pipelines/%s", name));
        return FelderaPipeline.fromJson(resp);
    }

    private void waitForCompilation(String name) throws Exception {
        List<String> wait = Arrays.asList("Pending", "CompilingSql", "SqlCompiled", "CompilingRust");

        while (true) {
            FelderaPipeline resp = this.getPipeline(name);
            String status = resp.getProgramStatus();

            if (Objects.equals(status, "Success")) {
                return;
            } else if (!wait.contains(status)) {
                throw new AssertionError(String.format("err: pipeline: %s failed to compile: %s", name, status));
            }

            Thread.sleep(500);
        }
    }

    public Map<String, Object> exec(String pipelineName, String sql) throws Exception {
        Map<String, String> options = new HashMap<>();
        options.put("sql", URLEncoder.encode(sql, StandardCharsets.UTF_8));
        options.put("format", "json");

        String resp = this.httpRequests.get(String.format("/pipelines/%s/query", pipelineName), options);
        if (resp.isBlank()) {
            return Collections.emptyMap();
        }

        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);

        return mapper.readValue(resp, mapType);
    }

    private void blockTillDesiredState(String pipelineName, String desired) throws Exception {
        while (true) {
            String deploymentStatus = this.getPipeline(pipelineName).getDeploymentStatus();
            if (deploymentStatus.equalsIgnoreCase(desired)) {
                break;
            }

            Thread.sleep(500);
        }
    }

    public void createPipeline(String pipelineName, String body) throws Exception {
        this.httpRequests.put(String.format("/pipelines/%s", pipelineName), body);
        waitForCompilation(pipelineName);
    }

    public void start(String pipelineName) throws Exception {
        this.httpRequests.post(String.format("/pipelines/%s/start", pipelineName), Collections.emptyMap());
        blockTillDesiredState(pipelineName, "running");
    }

    public void pause(String pipelineName) throws Exception {
        this.httpRequests.post(String.format("/pipelines/%s/pause", pipelineName), Collections.emptyMap());
        blockTillDesiredState(pipelineName, "paused");
    }

    public void shutdown(String pipelineName) throws Exception {
        this.httpRequests.post(String.format("/pipelines/%s/shutdown", pipelineName), Collections.emptyMap());
        blockTillDesiredState(pipelineName, "shutdown");
    }
}