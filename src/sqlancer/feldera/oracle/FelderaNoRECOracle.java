package sqlancer.feldera.oracle;

import sqlancer.Main;
import sqlancer.MainOptions;
import sqlancer.common.oracle.TestOracle;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.feldera.FelderaConnection;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;

public class FelderaNoRECOracle
        implements TestOracle<FelderaGlobalState> {

    protected final FelderaGlobalState state;
    protected final Main.StateLogger logger;
    protected final MainOptions options;
    protected final FelderaConnection con;
    @SuppressWarnings("unused")
    private final FelderaSchema schema;
    @SuppressWarnings("unused")
    private final ExpectedErrors errors;
    @SuppressWarnings("unused")
    private String lastQueryString;

    public FelderaNoRECOracle(FelderaGlobalState state) {
        this.schema = state.getSchema();
        this.errors = new ExpectedErrors();
        this.state = state;
        this.con = state.getConnection();
        this.logger = state.getLogger();
        this.options = state.getOptions();
    }

    @Override
    public void check() throws Exception {
    }
}
