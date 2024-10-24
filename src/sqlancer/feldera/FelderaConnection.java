package sqlancer.feldera;

import sqlancer.SQLancerDBConnection;
import sqlancer.feldera.client.FelderaClient;

import java.util.Objects;

public class FelderaConnection implements SQLancerDBConnection {
    private final FelderaClient client;
    private FelderaSchema schema;

    public FelderaConnection(FelderaClient client) {
        this.schema = new FelderaSchema(client.pipelineName());
        this.client = client;
    }

    public FelderaSchema getSchema() {
        return schema;
    }

    public FelderaClient getClient()  {
        return this.client;
    }

    @Override
    public String getDatabaseVersion() throws Exception {
        return Objects.requireNonNull(this.client.get()).get("version").toString();
    }

    @Override
    public void close() throws Exception {
        this.client.shutdown();
    }
}
