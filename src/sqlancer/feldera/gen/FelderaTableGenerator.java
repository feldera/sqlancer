package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.DBMSCommon;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.query.FelderaOtherQuery;

import java.util.ArrayList;
import java.util.List;

public class FelderaTableGenerator {
    protected final ExpectedErrors errors = new ExpectedErrors();
    private final String tableName;
    private final StringBuilder sb = new StringBuilder();
    private final List<FelderaSchema.FelderaColumn> columnsToBeAdded = new ArrayList<>();
    private FelderaSchema.FelderaTable table;

    public FelderaTableGenerator(String tableName) {
        this.tableName = tableName;
    }

    public FelderaOtherQuery generate() {
        table = new FelderaSchema.FelderaTable(tableName, columnsToBeAdded);

        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append("(");

        int nrColumns = Randomly.smallNumber() + 1;
        for (int i = 0; i < nrColumns; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            createField(DBMSCommon.createColumnName(i));
        }

        sb.append(") with ('materialized' = 'true');\n");
        return new FelderaOtherQuery(sb.toString(), new ExpectedErrors());
    }

    private void createField(String name) throws AssertionError {
        sb.append(name);
        sb.append(" ");
        FelderaSchema.FelderaDataType type = FelderaSchema.FelderaDataType.getRandomNonNullType();
        FelderaCommon.appendDataType(type, sb);
        FelderaSchema.FelderaFieldColumn c = new FelderaSchema.FelderaFieldColumn(name, type);
        c.setTable(table);
        columnsToBeAdded.add(c);
    }

    public FelderaSchema.FelderaTable getTable() {
        return this.table;
    }
}
