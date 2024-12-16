package sqlancer.feldera.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.Map;

public class TestFelderaClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final FelderaClient client = new FelderaClient("http://localhost:8080");

    @Test
    public void testCreatePipeline() throws Exception {
        String name = "testpipeline0";

        ObjectNode node = mapper.createObjectNode();
        node.put("name", name);
        node.put("description", "sqlancerTest");
        node.put("program_code", "");
        node.putObject("runtime_config");
        node.putObject("program_config");

        client.createPipeline(name, node.toString());
    }

    @Test
    public void testGetPipeline() throws Exception {
        String name = "testpipeline0";
        FelderaPipeline p = client.getPipeline(name);

        assert p.getName().equals(name);
    }

    @Test
    public void testStateChanges() throws Exception {
        String name = "testpipeline0";
        client.start(name);
        client.pause(name);
        client.shutdown(name);
    }

    @Test
    public void testSelect() throws Exception {
        String name = "testpipeline0";
        client.start(name);
        Map<String, Object> resp = client.exec(name, "select 1 as c");
        client.shutdown(name);

        assert (Integer) resp.get("c") == 1;
    }
}
