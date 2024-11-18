package sqlancer.feldera;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sqlancer.Randomly;
import sqlancer.common.schema.*;
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

    @Override
    public String toString() {
        StringBuilder dbg = new StringBuilder("FelderaSchema{" +
                "pipelineName='" + pipelineName + '\n');

        for (FelderaTable tbl: this.getDatabaseTablesWithoutViews()) {
            dbg.append(tbl.toString());
        }

        dbg.append('}');

        return dbg.toString();
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
        INT, BOOLEAN, VARCHAR, DOUBLE;

        public static FelderaDataType getRandomType() {
            return Randomly.fromOptions(values());
        }

        private static double round(double number, int places) {
            BigDecimal decimal = new BigDecimal(number);
            decimal = decimal.setScale(places, RoundingMode.HALF_UP);
            return decimal.doubleValue();
        }

        public FelderaExpression getRandomConstant(FelderaGlobalState globalState) {
            if (Randomly.getBooleanWithSmallProbability()) {
                return FelderaConstant.createNullConstant();
            }
            switch (this) {
                case INT:
                    return FelderaConstant.createIntConstant(globalState.getRandomly().getInteger());
                case DOUBLE:
                    // TODO: support infinite doubles
                    return FelderaConstant.createDoubleConstant(
                            round(globalState.getRandomly().getFiniteDouble(), 10)
                    );
                case VARCHAR:
                    return FelderaConstant.createVarcharConstant(
                            globalState.getRandomly().getString().replaceAll("[^\\x00-\\x7F]", "")
                    );
                case BOOLEAN:
                    return FelderaConstant.createBooleanConstant(Randomly.getBoolean());
                default:
                    throw new AssertionError(this);
            }
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

        public boolean isNullable() {
            return isNullable;
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

        public static List<FelderaColumn> getAllColumns(List<FelderaTable> tables) {
            return tables.stream().map(AbstractTable::getColumns).flatMap(List::stream).collect(Collectors.toList());
        }
    }

}
