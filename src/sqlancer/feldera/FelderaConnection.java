package sqlancer.feldera;

import sqlancer.SQLancerDBConnection;
import sqlancer.feldera.client.FelderaClient;

public class FelderaConnection implements SQLancerDBConnection {
    private final FelderaClient client;

    public FelderaConnection(FelderaClient client) {
        this.client = client;
    }

    public FelderaClient getClient()  {
        return this.client;
    }

    @Override
    public String getDatabaseVersion() throws Exception {
        this.client.get();
        return "asda";
    }

    @Override
    public void close() throws Exception {

    }
}
