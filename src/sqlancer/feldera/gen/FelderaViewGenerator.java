package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaToStringVisitor;
import sqlancer.feldera.ast.FelderaSelect;
import sqlancer.feldera.query.FelderaOtherQuery;

public class FelderaViewGenerator {
    private static int counter = 0;
    public FelderaViewGenerator() {
    }

    public static FelderaOtherQuery generate(FelderaGlobalState globalState) {
        ExpectedErrors errors = new ExpectedErrors();
        StringBuilder sb = new StringBuilder("CREATE MATERIALIZED VIEW ");
        sb.append("v");
        sb.append(counter++);
        int nrColumns = Randomly.smallNumber() + 1;
        sb.append("(");
        for (int i = 0; i < nrColumns; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(DBMSCommon.createColumnName(i));
        }
        sb.append(") AS (");
        FelderaSelect select = FelderaRandomQueryGenerator.createRandomQuery(nrColumns, globalState);
        sb.append(FelderaToStringVisitor.asString(select));
        sb.append(");\n");
        return new FelderaOtherQuery(sb.toString(), errors);
    }
}
