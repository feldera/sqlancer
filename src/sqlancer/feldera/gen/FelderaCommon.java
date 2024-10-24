package sqlancer.feldera.gen;

import sqlancer.feldera.FelderaSchema;

public class FelderaCommon {
    private FelderaCommon() {}

    public static void appendDataType(FelderaSchema.FelderaDataType type, StringBuilder sb) throws AssertionError {
        switch (type) {
            case BOOLEAN:
                sb.append("BOOLEAN");
                break;
            case INT:
                sb.append("INT");
                break;
            case VARCHAR:
                sb.append("VARCHAR");
                break;
            case DOUBLE:
                sb.append("DOUBLE");
                break;
            default:
                throw new AssertionError(type);
        }
    }
}
