package sqlancer.feldera.oracle;

import sqlancer.Main;
import sqlancer.MainOptions;
import sqlancer.common.oracle.TestOracle;
import sqlancer.feldera.FelderaConnection;
import sqlancer.feldera.FelderaGlobalState;

import java.util.HashMap;

public class FelderaNoRECOracle implements TestOracle<FelderaGlobalState> {

    protected final FelderaGlobalState state;
    protected final Main.StateLogger logger;
    protected final MainOptions options;
    protected final FelderaConnection con;

    public FelderaNoRECOracle(FelderaGlobalState state) {
        this.state = state;
        this.con = state.getConnection();
        this.logger = state.getLogger();
        this.options = state.getOptions();
    }

    @Override
    public void check() throws Exception {
        for (String view : state.getViews()) {
            String query = String.format("select * from %s except select * from %s_optimized", view, view);
            HashMap<String, Object> ret = con.getClient().executeSelect(query);
            if (!ret.isEmpty()) {
                throw new AssertionError("query failed: " + query);
            }
        }
    }
}
