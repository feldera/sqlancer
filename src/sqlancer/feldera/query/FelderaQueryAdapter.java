package sqlancer.feldera.query;

import sqlancer.GlobalState;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.Query;
import sqlancer.feldera.FelderaConnection;

public class FelderaQueryAdapter extends Query<FelderaConnection> {
    @Override
    public String getLogString() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getUnterminatedQueryString() {
        return null;
    }

    @Override
    public boolean couldAffectSchema() {
        return false;
    }

    @Override
    public <G extends GlobalState<?, ?, FelderaConnection>> boolean execute(G globalState, String... fills) throws Exception {
        return false;
    }

    @Override
    public ExpectedErrors getExpectedErrors() {
        return null;
    }
}
