package sqlancer.feldera;

import sqlancer.ExecutionTimer;
import sqlancer.GlobalState;
import sqlancer.common.query.Query;

import java.util.ArrayList;
import java.util.List;

public class FelderaGlobalState extends GlobalState<FelderaOptions, FelderaSchema, FelderaConnection> {
    List<String> views = new ArrayList<>();

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
    public void updateSchema() throws Exception {
        ; // do nothing
    }

    @Override
    public FelderaSchema getSchema() {
        return super.getSchema();
    }

    @Override
    protected FelderaSchema readSchema() throws Exception {
        return FelderaSchema.fromConnection(getConnection());
    }

    public void addTable(FelderaSchema.FelderaTable table) {
        FelderaSchema sch = getSchema();
        if (sch == null) {
            sch = new FelderaSchema(getConnection().getClient().pipelineName());
        }
        setSchema(sch.addTable(table));
    }

    public void addView(String view) {
        this.views.add(view);
    }

    public List<String> getViews() {
        return this.views;
    }
}
