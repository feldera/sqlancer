package sqlancer.feldera.query;

import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.Query;
import sqlancer.feldera.FelderaConnection;

public abstract class FelderaQueryAdapter extends Query<FelderaConnection> {

    String query;
    ExpectedErrors errors;

    public FelderaQueryAdapter(String query, ExpectedErrors errors) {
        this.query = query;
        this.errors = errors;
    }

    @Override
    public String getLogString() {
        return query;
    }

    @Override
    public String getQueryString() {
        return query;
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
    public ExpectedErrors getExpectedErrors() {
        return errors;
    }
}
