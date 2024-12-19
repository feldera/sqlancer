package sqlancer.feldera;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sqlancer.SQLancerDBConnection;
import sqlancer.feldera.client.FelderaClient;
import sqlancer.feldera.client.FelderaPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FelderaConnection implements SQLancerDBConnection {
    private final FelderaClient client;
    private final String pipelineName;
    private final List<String> inserts;
    private boolean ready = false;
    private String ddl;

    public FelderaConnection(String url, String pipelineName) {
        this.client = new FelderaClient(url);
        this.pipelineName = pipelineName;
        this.inserts = new ArrayList<>();
        this.ddl = "";
    }

    public void prepare() throws Exception {
        if (!ready) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("name", this.pipelineName);
            node.put("description", "sqlancerTest");
            String ddlWithInserts = ddl + "--" + String.join("\n--", inserts);
            node.put("program_code", ddlWithInserts);
            node.putObject("runtime_config");
            node.putObject("program_config");

            this.client.createPipeline(pipelineName, node.toString());
            this.client.start(this.pipelineName);

            for (String insert : this.inserts) {
                this.client.exec(this.pipelineName, insert);
            }

            ready = true;
        }
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public FelderaClient getClient() {
        return this.client;
    }

    @Override
    public String getDatabaseVersion() throws Exception {
        int x = this.client.getPipeline(this.pipelineName).getVersion();
        return Integer.toString(x);
    }

    @Override
    public void close() throws Exception {
        if (ready) {
            this.client.shutdown(pipelineName);
        }
    }

    public FelderaPipeline get() throws Exception {
        return this.client.getPipeline(pipelineName);
    }

    public void buffer(String query) throws Exception {
        if (query.startsWith("INSERT")) {
            this.inserts.add(query);
        } else {
            this.ddl += query;
        }
    }

    public Map<String, Object> execute(String query) throws Exception {
        return this.client.exec(this.pipelineName, query);
    }

    public void shutdown() throws Exception {
        this.client.shutdown(this.pipelineName);
    }
}
