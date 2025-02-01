package sqlancer.feldera;

import java.util.*;
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

    public static FelderaSchema fromConnection(FelderaConnection con) throws Exception {
        return new FelderaSchema(new ArrayList<>(), con.getPipelineName());
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
        BOOLEAN, INT, VARCHAR, CHAR, NULL, TIME, DATE, TIMESTAMP, DECIMAL, FLOAT, ANY,
        // VARIANT,
        // VARBINARY,
        // INTERVAL,
        // GEOMETRY,
        // ROW,
        // MAP,
        ARRAY;

        public static FelderaDataType getRandomNumericType() {
            return Randomly
                    .fromList(Arrays.stream(values()).filter(FelderaDataType::isNumeric).collect(Collectors.toList()));
        }

        public boolean isNumeric() {
            switch (this) {
            case FLOAT:
            case INT:
            case DECIMAL:
                return true;
            default:
                return false;
            }
        }

        public static FelderaDataType getRandomNonNullType() {
            return Randomly.fromList(Arrays.stream(values())
                    .filter(t -> t != FelderaDataType.NULL && t != FelderaDataType.ANY).collect(Collectors.toList()));
        }

        public static FelderaDataType[] nonNullValues() {
            return Arrays.stream(values()).filter(t -> t != FelderaDataType.NULL && t != FelderaDataType.ANY)
                    .toArray(FelderaDataType[]::new);
        }

        public static FelderaDataType getRandomType() {
            return Randomly.fromOptions(values());
        }
    }

    public static class FelderaCompositeDataType {
        private final FelderaDataType dataType;
        private final int size;
        private final int scale;
        private final FelderaCompositeDataType elementType;

        public FelderaCompositeDataType(FelderaDataType dataType, int size, int scale) {
            this.dataType = dataType;
            this.size = size;
            this.scale = scale;
            this.elementType = null;
        }

        public static FelderaCompositeDataType arrayOf(FelderaDataType elementType) {
            return new FelderaCompositeDataType(FelderaDataType.ARRAY, getRandomFromPrimitiveType(elementType));
        }

        public FelderaCompositeDataType(FelderaDataType dataType, FelderaCompositeDataType elementType) {
            if (dataType != FelderaDataType.ARRAY) {
                throw new IllegalArgumentException("dataType must be ARRAY");
            }

            this.dataType = dataType;
            this.scale = -1;
            this.size = -1;
            this.elementType = elementType;
        }

        public FelderaExpression getRandomConstant(FelderaGlobalState globalState) {
            if (Randomly.getBooleanWithSmallProbability()) {
                return FelderaConstant.createNullConstant();
            }

            return FelderaConstant.getRandomConstant(globalState, this);
        }

        public boolean isNumeric() {
            return this.getPrimitiveType().isNumeric();
        }

        public FelderaCompositeDataType getElementType() {
            return this.elementType;
        }

        public static FelderaCompositeDataType getBooleanType() {
            return FelderaCompositeDataType.getRandomFromPrimitiveType(FelderaDataType.BOOLEAN);
        }

        public static FelderaCompositeDataType getRandomVarcharType() {
            return FelderaCompositeDataType.getRandomFromPrimitiveType(FelderaDataType.VARCHAR);
        }

        public static FelderaCompositeDataType getRandomNumericType() {
            FelderaDataType type = FelderaDataType.getRandomNumericType();
            return FelderaCompositeDataType.getRandomFromPrimitiveType(type);
        }

        public FelderaDataType getPrimitiveType() {
            return dataType;
        }

        public int getSize() {
            return size;
        }

        public int getScale() {
            return scale;
        }

        public boolean isArray() {
            return dataType == FelderaDataType.ARRAY;
        }

        public static FelderaCompositeDataType getRandomFromPrimitiveType(FelderaDataType type) {
            int size = -1;
            int scale = -1;
            switch (type) {
            case FLOAT:
                size = Randomly.fromOptions(32, 64);
                return new FelderaCompositeDataType(type, size, scale);
            case INT:
                size = Randomly.fromOptions(8, 16, 32, 64);
                return new FelderaCompositeDataType(type, size, scale);
            case ARRAY:
                return new FelderaCompositeDataType(type, FelderaCompositeDataType.getRandomWithoutNull());
            case DECIMAL:
                scale = (int) Randomly.getNotCachedInteger(0, 10);
                size = (int) Randomly.getNotCachedInteger(scale, 25);
                return new FelderaCompositeDataType(type, size, scale);
            case CHAR:
                size = (int) Randomly.getNotCachedInteger(1, 10);
                return new FelderaCompositeDataType(type, size, scale);
            case VARCHAR:
                if (Randomly.getBoolean()) {
                    size = (int) Randomly.getNotCachedInteger(1, 30);
                }
                return new FelderaCompositeDataType(type, size, scale);
            default:
                return new FelderaCompositeDataType(type, size, scale);
            }
        }

        public static FelderaCompositeDataType getRandomWithoutNull() {
            FelderaDataType type = FelderaDataType.getRandomNonNullType();
            return FelderaCompositeDataType.getRandomFromPrimitiveType(type);
        }

        @Override
        public String toString() {
            switch (dataType) {
            case INT:
                switch (size) {
                case 8:
                    return "TINYINT";
                case 16:
                    return "SMALLINT";
                case 32:
                    return "INT";
                case 64:
                    return "BIGINT";
                default:
                    throw new AssertionError(dataType.toString() + scale);
                }
            case FLOAT:
                switch (size) {
                case 32:
                    return "REAL";
                case 64:
                    return "DOUBLE";
                default:
                    throw new AssertionError(dataType.toString() + scale);
                }
            case ARRAY:
                if (elementType == null) {
                    throw new AssertionError(this);
                }
                return elementType + " ARRAY";
            case CHAR:
                return "CHAR(" + size + ")";
            case VARCHAR:
                if (size == -1) {
                    return "VARCHAR";
                }
                return "VARCHAR(" + size + ")";
            case DECIMAL:
                return "DECIMAL(" + size + ", " + scale + ")";
            default:
                return dataType.toString();
            }
        }
    }

    public static class FelderaFieldColumn extends FelderaColumn {
        public FelderaFieldColumn(String name, FelderaCompositeDataType columnType) {
            super(name, columnType);
        }

        public FelderaFieldColumn(String name, FelderaCompositeDataType columnType, boolean isNullable) {
            super(name, columnType, isNullable);
            // Note to self: later, assert that the Field column isn't something like INTERVAL
        }
    }

    public static class FelderaColumn extends AbstractTableColumn<FelderaTable, FelderaCompositeDataType> {

        private final boolean isNullable;

        public FelderaColumn(String name, FelderaCompositeDataType columnType) {
            super(name, null, columnType);
            this.isNullable = false;
        }

        public FelderaColumn(String name, FelderaCompositeDataType columnType, boolean isNullable) {
            super(name, null, columnType);
            this.isNullable = isNullable;
        }

        public FelderaColumnReference asColumnReference() {
            return new FelderaColumnReference(this);
        }

        public static FelderaColumn createDummy(String name) {
            return new FelderaColumn(name, FelderaCompositeDataType.getRandomWithoutNull());
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

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FelderaTable) {
                FelderaTable other = (FelderaTable) obj;
                return Objects.equals(this.name, other.name) && this.getColumns() == other.getColumns();
            } else {
                return false;
            }
        }

        public static List<FelderaColumn> getAllColumns(List<FelderaTable> tables) {
            return tables.stream().map(AbstractTable::getColumns).flatMap(List::stream).collect(Collectors.toList());
        }
    }

}
