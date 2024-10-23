package sqlancer.feldera.oracle;

import sqlancer.Main;
import sqlancer.MainOptions;
import sqlancer.common.oracle.TestOracle;
import sqlancer.feldera.FelderaConnection;
import sqlancer.feldera.FelderaGlobalState;

public abstract class FelderaNoRECBase implements TestOracle<FelderaGlobalState> {
    protected final FelderaGlobalState state;
    protected final Main.StateLogger logger;
    protected final MainOptions options;
    protected final FelderaConnection con;
    protected String optimizedQueryString;
    protected String unoptimizedQueryString;

    public FelderaNoRECBase(FelderaGlobalState state) {
        this.state = state;
        this.con = state.getConnection();
        this.logger = state.getLogger();
        this.options = state.getOptions();
    }
}
