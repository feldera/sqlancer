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
    private static int counter = 0;

    public FelderaViewGenerator() {
    }

    private static String createViewName(int n) {
        return "v" + n;
    }

    public static List<FelderaOtherQuery> generate(FelderaGlobalState globalState) {
        List<FelderaOtherQuery> queries = new ArrayList<>();
        FelderaExpressionGenerator gen = new FelderaExpressionGenerator(globalState)
                .setTablesAndColumns(TestOracleUtils.getRandomTableNonEmptyTables(globalState.getSchema()));
        FelderaSelect select = gen.generateSelect();
        FelderaExpression whereCondition = gen.generateBooleanExpression();

        queries.add(generateViewFromSelect(select, gen, whereCondition, counter, true));
        queries.add(generateViewFromSelect(select, gen, whereCondition, counter, false));
        globalState.addView(createViewName(counter++));
        return queries;
    }

    private static FelderaOtherQuery generateViewFromSelect(FelderaSelect select, FelderaExpressionGenerator gen,
            FelderaExpression whereCondition, int viewNumber, boolean optimized) {
        ExpectedErrors errors = new ExpectedErrors();
        StringBuilder sb = new StringBuilder("CREATE MATERIALIZED VIEW ");
        String name = createViewName(viewNumber);
        sb.append(name);
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
