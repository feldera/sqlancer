package sqlancer.feldera.oracle;

import sqlancer.common.oracle.TestOracle;
import sqlancer.feldera.FelderaGlobalState;

public class FelderaNoRECOracle extends FelderaNoRECBase implements TestOracle<FelderaGlobalState> {
    public FelderaNoRECOracle(FelderaGlobalState state) {
        super(state);
    }

    @Override
    public void check() throws Exception {

    }
}
