package sqlancer.feldera;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sqlancer.Randomly;
import sqlancer.common.schema.AbstractRowValue;
import sqlancer.common.schema.AbstractSchema;
import sqlancer.common.schema.AbstractTable;
import sqlancer.common.schema.AbstractTableColumn;
import sqlancer.common.schema.AbstractTables;
import sqlancer.common.schema.TableIndex;
import sqlancer.feldera.ast.FelderaConstant;

public class FelderaSchema extends AbstractSchema<FelderaGlobalState, FelderaSchema.FelderaTable> {

    private final String databaseName;

    public FelderaSchema(List<FelderaTable> databaseTables, String databaseName) {
        super(databaseTables);
        this.databaseName = databaseName;
    }

    public static FelderaDataType getColumnType(String typeString) {
        switch (typeString.toLowerCase()) {
            case "bigint":
                return FelderaDataType.INT;
            case "boolean":
                return FelderaDataType.BOOLEAN;
            case "varchar":
                return FelderaDataType.VARCHAR;
            case "double":
                return FelderaDataType.DOUBLE;
            default:
                throw new AssertionError(typeString);
        }
    }

    public static FelderaSchema fromConnection(FelderaConnection con) throws Exception {
        return null;
    }

    protected static List<FelderaColumn> getTableColumns(FelderaConnection con, String tableName) throws Exception {
        return null;
    }

    public FelderaTables getRandomTableNonEmptyTables() {
        return new FelderaTables(Randomly.nonEmptySubset(getDatabaseTables()));
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public enum FelderaDataType {
        INT, BOOLEAN, VARCHAR, DOUBLE;

        public static FelderaDataType getRandomType() {
            return Randomly.fromOptions(values());
        }
    }

    public static class FelderaColumn extends AbstractTableColumn<FelderaTable, FelderaDataType> {

        public FelderaColumn(String name, FelderaDataType columnType) {
            super(name, null, columnType);
        }

        public static FelderaColumn createDummy(String name) {
            return new FelderaColumn(name, FelderaDataType.INT);
        }

    }

    public static class FelderaTables extends AbstractTables<FelderaTable, FelderaColumn> {

        public FelderaTables(List<FelderaTable> tables) {
            super(tables);
        }

        public FelderaRowValue getRandomRowValue(FelderaConnection con) {
            return null;
        }

    }

    public static class FelderaRowValue extends AbstractRowValue<FelderaTables, FelderaColumn, FelderaConstant> {

        protected FelderaRowValue(FelderaTables tables, Map<FelderaColumn, FelderaConstant> values) {
            super(tables, values);
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

        @Override
        public List<FelderaColumn> getRandomNonEmptyColumnSubset() {
//            List<FelderaColumn> selectedColumns = new ArrayList<>();
//            ArrayList<FelderaColumn> remainingColumns = new ArrayList<>(this.getColumns());


            // TODO

            return new ArrayList<>();
        }
    }

}
