package sqlancer.feldera.gen;

import sqlancer.feldera.FelderaSchema;

public class FelderaCommon {
    private FelderaCommon() {
    }

    public static void appendDataType(FelderaSchema.FelderaDataType type, StringBuilder sb) throws AssertionError {
        sb.append(type.toString());
    }
}
