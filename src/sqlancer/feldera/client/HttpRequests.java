package sqlancer.feldera.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;

public class HttpRequests {
    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public HttpRequests(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    private HttpRequest.Builder createRequestBuilder(String path) {
        return HttpRequest.newBuilder().timeout(Duration.ofSeconds(2)).uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json").header("User-Agent", "feldera-java-client/v1");
    }

    private <T> HttpRequest.BodyPublisher serializeBody(T body) throws JsonProcessingException {
        if (body == null) {
            return HttpRequest.BodyPublishers.noBody();
        } else if (body instanceof String) {
            return HttpRequest.BodyPublishers.ofString((String) body);
        } else {
            return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body));
        }
    }

    private void validateResponse(HttpResponse<String> response) throws Exception {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return; // Successful response
        }

        String contentType = response.headers().firstValue("content-type").orElse("");
        String errorMessage;

        if (contentType.equals("application/json")) {
            errorMessage = objectMapper.readTree(response.body()).toPrettyString();
        } else {
            errorMessage = response.body();
        }

        throw new Exception("HTTP Error: " + statusCode + " " + errorMessage);
    }

    private String sendRequest(HttpRequest request) throws Exception {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validateResponse(response);
            return response.body();
        } catch (HttpTimeoutException e) {
            throw new Exception("Request timed out: " + e.getMessage(), e);
        } catch (Exception e) {
            String path = request.uri().getPath();
            throw new Exception("Failed to send request: " + path + "\nErr: " + e.getMessage(), e);
        }
    }

    public String get(String path) throws Exception {
        HttpRequest request = createRequestBuilder(path).GET().build();
        return sendRequest(request);
    }

    public String get(String path, Map<String, String> queryParams) throws Exception {
        if (queryParams == null) {
            return get(path);
        }

        String q = "?" + queryParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b).orElse("");

        HttpRequest request = createRequestBuilder(path + q).GET().build();
        return sendRequest(request);
    }

    public <T> String post(String path, T body) throws Exception {
        HttpRequest request = createRequestBuilder(path).POST(serializeBody(body)).build();

        return sendRequest(request);
    }

    public <T> String patch(String path, T body) throws Exception {
        HttpRequest request = createRequestBuilder(path).method("PATCH", serializeBody(body)).build();

        return sendRequest(request);
    }

    public <T> String put(String path, T body) throws Exception {
        HttpRequest request = createRequestBuilder(path).PUT(serializeBody(body)).build();

        return sendRequest(request);
    }
}
