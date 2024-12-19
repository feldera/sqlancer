package sqlancer.feldera;

import sqlancer.OracleFactory;
import sqlancer.feldera.oracle.FelderaNoRECOracle;
import sqlancer.common.oracle.TestOracle;

public enum FelderaOracleFactory implements OracleFactory<FelderaGlobalState> {
    NOREC {
        @Override
        public TestOracle<FelderaGlobalState> create(FelderaGlobalState globalState) {
            return new FelderaNoRECOracle(globalState);
        }
    },
}