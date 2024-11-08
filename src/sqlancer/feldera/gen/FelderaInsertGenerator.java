package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.schema.AbstractTableColumn;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.FelderaToStringVisitor;
import sqlancer.feldera.ast.FelderaExpression;
import sqlancer.feldera.query.FelderaOtherQuery;

import java.util.List;
import java.util.stream.Collectors;

public class FelderaInsertGenerator {
    private final FelderaGlobalState globalState;
    private final ExpectedErrors errors = new ExpectedErrors();

    private FelderaInsertGenerator(FelderaGlobalState globalState) {
        this.globalState = globalState;
    }

    public static FelderaOtherQuery insert(FelderaGlobalState globalState) {
        return new FelderaInsertGenerator(globalState).generate();
    }

    private FelderaOtherQuery generate() {
        FelderaSchema.FelderaTable table = globalState.getSchema().getRandomTable(t -> !t.isView());
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO ");
        List<FelderaSchema.FelderaColumn> columns = table.getColumns();
        sb.append(table.getName());
        sb.append("(");
        sb.append(columns.stream().map(AbstractTableColumn::getName).collect(Collectors.joining(", ")));
        sb.append(")");
        sb.append(" VALUES ");

        int n = Randomly.smallNumber() + 1;
        for (int i = 0; i < n; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            insertRow(globalState, sb, columns);
        }

        sb.append(";");
        return new FelderaOtherQuery(sb.toString(), errors);
    }

    private static void insertRow(FelderaGlobalState globalState, StringBuilder sb, List<FelderaSchema.FelderaColumn> columns) {
        sb.append("(");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            FelderaExpression generateConstant = new FelderaExpressionGenerator(globalState).generateConstant(columns.get(i).getType());
            sb.append(FelderaToStringVisitor.asString(generateConstant));
        }
        sb.append(")");
    }
}
