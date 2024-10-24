package sqlancer.feldera.query;

import sqlancer.GlobalState;
import sqlancer.IgnoreMeException;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.feldera.FelderaConnection;

public class FelderaOtherQuery extends FelderaQueryAdapter {
    public FelderaOtherQuery(String query, ExpectedErrors errors) {
        super(query, errors);
    }

    @Override
    public boolean couldAffectSchema() {
        return true;
    }

    @Override
    public <G extends GlobalState<?, ?, FelderaConnection>> boolean execute(G globalState, String... fills)
            throws Exception {
        try {
            globalState.getConnection().getClient().buffer(query);
        } catch (Exception e) {
            if (this.errors.errorIsExpected(e.getMessage())) {
                throw new IgnoreMeException();
            }
        }
        return true;
    }
}
