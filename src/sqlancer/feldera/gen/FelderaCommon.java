package sqlancer.feldera.gen;

import sqlancer.feldera.FelderaSchema;

public class FelderaCommon {
    private FelderaCommon() {
    }

    public static void appendDataType(FelderaSchema.FelderaDataType type, StringBuilder sb) throws AssertionError {
        switch (type) {
        case BOOLEAN:
            sb.append("BOOLEAN");
            break;
        case TINYINT:
            sb.append("TINYINT");
            break;
        case SMALLINT:
            sb.append("SMALLINT");
            break;
        case INT:
            sb.append("INT");
            break;
        case BIGINT:
            sb.append("BIGINT");
            break;
        case VARCHAR:
            sb.append("VARCHAR");
            break;
        case CHAR:
            sb.append("CHAR");
            break;
        case NULL:
            sb.append("NULL");
            break;
        case TIME:
            sb.append("TIME");
            break;
        case DATE:
            sb.append("DATE");
            break;
        case TIMESTAMP:
            sb.append("TIMESTAMP");
            break;
        case REAL:
            sb.append("REAL");
            break;
        case DOUBLE:
            sb.append("DOUBLE");
            break;
        default:
            throw new AssertionError(type);
        }
    }
}
