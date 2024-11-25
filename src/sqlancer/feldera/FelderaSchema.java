package sqlancer.feldera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.schema.*;
import sqlancer.feldera.ast.FelderaColumnReference;
import sqlancer.feldera.ast.FelderaConstant;
import sqlancer.feldera.ast.FelderaExpression;

public class FelderaSchema extends AbstractSchema<FelderaGlobalState, FelderaSchema.FelderaTable> {

    private final String pipelineName;

    public FelderaSchema(List<FelderaTable> databaseTables, String pipelineName) {
        super(databaseTables);
        this.pipelineName = pipelineName;
    }

    public FelderaSchema(String pipelineName) {
        super(new ArrayList<>());
        this.pipelineName = pipelineName;
    }

    public FelderaSchema addTable(FelderaTable table) {
        List<FelderaTable> tables = new ArrayList<>(this.getDatabaseTables());
        tables.add(table);

        return new FelderaSchema(tables, this.pipelineName);
    }

    public static FelderaDataType getColumnType(String typeString) {
        switch (typeString.toUpperCase()) {
        case "BOOLEAN":
            return FelderaDataType.BOOLEAN;
        case "TINYINT":
            return FelderaDataType.TINYINT;
        case "SMALLINT":
            return FelderaDataType.SMALLINT;
        case "INT":
            return FelderaDataType.INT;
        case "BIGINT":
            return FelderaDataType.BIGINT;
        case "VARCHAR":
            return FelderaDataType.VARCHAR;
        case "CHAR":
            return FelderaDataType.CHAR;
        case "NULL":
            return FelderaDataType.NULL;
        case "TIME":
            return FelderaDataType.TIME;
        case "DATE":
            return FelderaDataType.DATE;
        case "TIMESTAMP":
            return FelderaDataType.TIMESTAMP;
        case "REAL":
            return FelderaDataType.REAL;
        case "DOUBLE":
            return FelderaDataType.DOUBLE;
        default:
            throw new AssertionError(typeString);
        }
    }

    public static FelderaSchema fromConnection(FelderaConnection con) throws Exception {
        return new FelderaSchema(new ArrayList<>(), con.getClient().pipelineName());
    }

    protected List<FelderaColumn> getTableColumns(String tableName) throws Exception {
        return this.getDatabaseTable(tableName).getColumns();
    }

    public FelderaTables getRandomTableNonEmptyTables() {
        return new FelderaTables(Randomly.nonEmptySubset(getDatabaseTables()));
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public enum FelderaDataType {
        BOOLEAN, TINYINT, SMALLINT, INT, BIGINT, VARCHAR, CHAR, NULL, TIME, DATE, TIMESTAMP,
        // DECIMAL,
        // VARBINARY,
        // INTERVAL,
        // GEOMETRY,
        // ROW,
        // ARRAY,
        // MAP,
        // VARIANT,
        REAL, DOUBLE;

        public static FelderaDataType getRandomNumericType() {
            return Randomly
                    .fromList(Arrays.stream(values()).filter(FelderaDataType::isNumeric).collect(Collectors.toList()));
        }

        public boolean isNumeric() {
            switch (this) {
            case REAL:
            case DOUBLE:
            case TINYINT:
            case SMALLINT:
            case INT:
            case BIGINT:
                return true;
            default:
                return false;
            }
        }

        public static FelderaDataType getRandomNonNullType() {
            return Randomly.fromList(
                    Arrays.stream(values()).filter(t -> t != FelderaDataType.NULL).collect(Collectors.toList()));
        }

        public static FelderaDataType getRandomType() {
            return Randomly.fromOptions(values());
        }

        public FelderaExpression getRandomConstant(FelderaGlobalState globalState) {
            if (Randomly.getBooleanWithSmallProbability()) {
                return FelderaConstant.createNullConstant();
            }

            return FelderaConstant.getRandomConstant(globalState, this);
        }
    }

    public static class FelderaFieldColumn extends FelderaColumn {
        public FelderaFieldColumn(String name, FelderaDataType columnType) {
            super(name, columnType);
        }

        public FelderaFieldColumn(String name, FelderaDataType columnType, boolean isNullable) {
            super(name, columnType, isNullable);
            // later, you can assert that the Field column isn't something like INTERVAL
        }
    }

    public static class FelderaColumn extends AbstractTableColumn<FelderaTable, FelderaDataType> {

        private final boolean isNullable;

        public FelderaColumn(String name, FelderaDataType columnType) {
            super(name, null, columnType);
            this.isNullable = false;
        }

        public FelderaColumn(String name, FelderaDataType columnType, boolean isNullable) {
            super(name, null, columnType);
            this.isNullable = isNullable;
        }

        public FelderaColumnReference asColumnReference() {
            return new FelderaColumnReference(this);
        }

        public static FelderaColumn createDummy(String name) {
            return new FelderaColumn(name, FelderaDataType.getRandomType());
        }

        public boolean isNullable() {
            return isNullable;
        }
    }

    public static class FelderaTables extends AbstractTables<FelderaTable, FelderaColumn> {

        public FelderaTables(List<FelderaTable> tables) {
            super(tables);
        }
    }

    public static class FelderaTable extends AbstractTable<FelderaColumn, TableIndex, FelderaGlobalState> {

        public FelderaTable(String tableName, List<FelderaColumn> columns) {
            super(tableName, columns, null, false);
        }

        // SELECT COUNT(*) FROM table;
        @Override
        public long getNrRows(FelderaGlobalState globalState) {
            // TODO
            return 0;
        }

        public static List<FelderaColumn> getAllColumns(List<FelderaTable> tables) {
            return tables.stream().map(AbstractTable::getColumns).flatMap(List::stream).collect(Collectors.toList());
        }
    }

}
