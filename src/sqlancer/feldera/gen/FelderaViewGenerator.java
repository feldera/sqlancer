package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.oracle.TestOracleUtils;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.ast.FelderaExpression;
import sqlancer.feldera.ast.FelderaSelect;
import sqlancer.feldera.query.FelderaOtherQuery;

import java.util.ArrayList;
import java.util.List;

public class FelderaViewGenerator {
    public FelderaViewGenerator() {
    }

    public static List<FelderaOtherQuery> generate(FelderaGlobalState globalState, String viewName) {
        List<FelderaOtherQuery> queries = new ArrayList<>();
        FelderaExpressionGenerator gen = new FelderaExpressionGenerator(globalState)
                .setTablesAndColumns(TestOracleUtils.getRandomTableNonEmptyTables(globalState.getSchema()));
        FelderaSelect select = gen.generateSelect();
        FelderaExpression whereCondition = gen.generateBooleanExpression();

        queries.add(generateViewFromSelect(select, gen, whereCondition, viewName, true));
        queries.add(generateViewFromSelect(select, gen, whereCondition, viewName, false));
        return queries;
    }

    private static FelderaOtherQuery generateViewFromSelect(FelderaSelect select, FelderaExpressionGenerator gen,
            FelderaExpression whereCondition, String viewName, boolean optimized) {
        ExpectedErrors errors = new ExpectedErrors();
        StringBuilder sb = new StringBuilder("CREATE MATERIALIZED VIEW ");
        sb.append(viewName);
        if (optimized) {
            sb.append("_optimized");
        }

        sb.append(" AS (");
        String selectQuery;

        if (optimized) {
            selectQuery = gen.generateOptimizedQueryString(select, whereCondition, Randomly.getBoolean());
        } else {
            selectQuery = gen.generateUnoptimizedQueryString(select, whereCondition);
        }
        sb.append(selectQuery);
        sb.append(");\n");
        return new FelderaOtherQuery(sb.toString(), errors);
    }
}
