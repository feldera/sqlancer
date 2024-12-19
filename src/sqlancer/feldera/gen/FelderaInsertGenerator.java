package sqlancer.feldera.gen;

import sqlancer.common.gen.AbstractInsertGenerator;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.schema.AbstractTableColumn;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.FelderaToStringVisitor;
import sqlancer.feldera.query.FelderaOtherQuery;

import java.util.List;
import java.util.stream.Collectors;

public class FelderaInsertGenerator extends AbstractInsertGenerator<FelderaSchema.FelderaColumn> {
    private final FelderaGlobalState globalState;
    private final ExpectedErrors errors = new ExpectedErrors();

    private FelderaInsertGenerator(FelderaGlobalState globalState) {
        this.globalState = globalState;
    }

    public static FelderaOtherQuery getQuery(FelderaGlobalState globalState) {
        return new FelderaInsertGenerator(globalState).generate();
    }

    private FelderaOtherQuery generate() {
        sb.append("INSERT INTO ");
        FelderaSchema.FelderaTable table = globalState.getSchema().getRandomTable(t -> !t.isView());
        List<FelderaSchema.FelderaColumn> columns = table
                .getRandomNonEmptyColumnSubsetFilter(p -> !p.getName().equals("rowid"));
        sb.append(table.getName());
        sb.append("(");
        sb.append(columns.stream().map(AbstractTableColumn::getName).collect(Collectors.joining(", ")));
        sb.append(")");
        sb.append(" VALUES ");
        insertColumns(columns);
        sb.append(";");
        String s = sb.toString();
        return new FelderaOtherQuery(s, errors);
    }

    @Override
    protected void insertValue(FelderaSchema.FelderaColumn column) {
        sb.append(FelderaToStringVisitor
                .asString(new FelderaExpressionGenerator(globalState).generateConstant(column.getType())));
    }
}
