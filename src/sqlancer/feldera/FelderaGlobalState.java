package sqlancer.feldera;

import sqlancer.ExecutionTimer;
import sqlancer.GlobalState;
import sqlancer.common.query.Query;

public class FelderaGlobalState extends GlobalState<FelderaOptions, FelderaSchema, FelderaConnection> {
    @Override
    protected void executeEpilogue(Query<?> q, boolean success, ExecutionTimer timer) throws Exception {
        boolean logExecutionTime = getOptions().logExecutionTime();
        if (success && getOptions().printSucceedingStatements()) {
            System.out.println(q.getQueryString());
        }
        if (logExecutionTime) {
            getLogger().writeCurrent(" -- " + timer.end().asString());
        }
        if (q.couldAffectSchema()) {
            updateSchema();
        }
    }

    @Override
    protected FelderaSchema readSchema() throws Exception {
        return FelderaSchema.fromConnection(getConnection());
    }
}
