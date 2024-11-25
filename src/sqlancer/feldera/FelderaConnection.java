package sqlancer.feldera;

import sqlancer.SQLancerDBConnection;
import sqlancer.feldera.client.FelderaClient;

import java.util.Objects;

public class FelderaConnection implements SQLancerDBConnection {
    private final FelderaClient client;

    public FelderaConnection(FelderaClient client) {
        this.client = client;
    }

    public FelderaClient getClient() {
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
