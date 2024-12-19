package sqlancer.feldera.ast;

import sqlancer.Randomly;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

public abstract class FelderaConstant implements FelderaExpression {
    private FelderaConstant() {
    }

    private static double round(double number, int places) {
        BigDecimal decimal = new BigDecimal(number);
        decimal = decimal.setScale(places, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    public static FelderaConstant getRandomConstant(FelderaGlobalState globalState,
            FelderaSchema.FelderaDataType type) {
        switch (type) {
        case BOOLEAN:
            return new FelderaBooleanConstant(Randomly.getBoolean());
        case TINYINT:
            return FelderaIntConstant.getRandom(globalState, 8);
        case SMALLINT:
            return FelderaIntConstant.getRandom(globalState, 16);
        case INT:
            return FelderaIntConstant.getRandom(globalState, 32);
        case BIGINT:
            return FelderaIntConstant.getRandom(globalState, 64);
        case VARCHAR:
            return FelderaVarcharConstant.getRandom(globalState);
        case CHAR:
            return FelderaCharConstant.getRandom(globalState);
        case NULL:
            return new FelderaNullConstant();
        case TIME:
            return FelderaTimeConstant.getRandom(globalState);
        case DATE:
            return FelderaDateConstant.getRandom(globalState);
        case TIMESTAMP:
            return FelderaTimestampConstant.getRandom(globalState);
        case REAL:
            return FelderaRealConstant.getRandom(globalState);
        case DOUBLE:
            return FelderaDoubleConstant.getRandom(globalState);
        default:
            throw new AssertionError(type);
        }
    }

    public static class FelderaTimeConstant extends FelderaConstant {
        private final String value;

        FelderaTimeConstant(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + value + "'";
        }

        public static FelderaTimeConstant getRandom(FelderaGlobalState globalState) {
            Randomly r = globalState.getRandomly();
            int h = r.getInteger(0, 23);
            int m = r.getInteger(0, 59);
            int s = r.getInteger(0, 59);

            return new FelderaTimeConstant(h + ":" + m + ":" + s);
        }
    }

    public static class FelderaDateConstant extends FelderaConstant {
        private final String value;

        FelderaDateConstant(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "'" + value + "'";
        }

        public static FelderaDateConstant getRandom(FelderaGlobalState globalState) {
            Randomly r = globalState.getRandomly();

            int year = r.getInteger(0, 9999);
            int month = r.getInteger(1, 12);
            int day = r.getInteger(1, 31);

            return new FelderaDateConstant(year + "-" + month + "-" + day);
        }
    }

    public static class FelderaTimestampConstant extends FelderaConstant {
        private final String value;

        FelderaTimestampConstant(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "'" + value + "'";
        }

        public static FelderaTimestampConstant getRandom(FelderaGlobalState globalState) {
            String date = FelderaDateConstant.getRandom(globalState).value;
            String time = FelderaTimeConstant.getRandom(globalState).value;

            return new FelderaTimestampConstant(date + " " + time);
        }
    }

    private static class FelderaNullConstant extends FelderaConstant {
        @Override
        public String toString() {
            return "NULL";
        }
    }

    public static class FelderaIntConstant extends FelderaConstant {
        private final long value;

        public FelderaIntConstant(long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public long getValue() {
            return value;
        }

        public static FelderaIntConstant getRandom(FelderaGlobalState globalState) {
            return new FelderaIntConstant(globalState.getRandomly().getInteger());
        }

        public static FelderaIntConstant getRandom(FelderaGlobalState globalState, int bitLength) {
            // int left = -(1 << (bitLength - 1));
            // int right = (1 << (bitLength - 1)) - 1;
            // return new FelderaIntConstant(globalState.getRandomly().getLong(left, right));
            // HACK: for now, generate just a small random number that isn't 0
            return new FelderaIntConstant(Randomly.smallNumber() + 1);
        }
    }

    public static class FelderaDoubleConstant extends FelderaConstant {
        private final double value;

        public FelderaDoubleConstant(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            if (value == Double.POSITIVE_INFINITY) {
                return "'+Inf'";
            } else if (value == Double.NEGATIVE_INFINITY) {
                return "'-Inf'";
            }
            return String.valueOf(value);
        }

        public static FelderaDoubleConstant getRandom(FelderaGlobalState globalState) {
            return new FelderaDoubleConstant(
                    FelderaConstant.round(globalState.getRandomly().getFiniteDouble() + 1.0, 10));
        }
    }

    public static class FelderaRealConstant extends FelderaConstant {
        private final float value;

        public FelderaRealConstant(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        @Override
        public String toString() {
            if (value == Float.POSITIVE_INFINITY) {
                return "'+Inf'";
            } else if (value == Float.NEGATIVE_INFINITY) {
                return "'-Inf'";
            }
            return String.valueOf(value);
        }

        public static FelderaRealConstant getRandom(FelderaGlobalState globalState) {
            return new FelderaRealConstant(
                    ((float) FelderaConstant.round(globalState.getRandomly().getFiniteDouble() + 1.0, 5)));
        }
    }

    public static class FelderaVarcharConstant extends FelderaConstant {
        private final String value;

        public FelderaVarcharConstant(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + value.replace("'", "''") + "'";
        }

        private static String getRandomString(FelderaGlobalState globalState) {
            return globalState.getRandomly().getString().replaceAll("[^a-zA-Z0-9]", "");
        }

        public static FelderaVarcharConstant getRandom(FelderaGlobalState globalState) {
            String randomString = getRandomString(globalState);

            // retry for 10 times, but if it's still empty, just use a default string
            for (int i = 0; i < 10; i++) {
                if (!randomString.isBlank()) {
                    break;
                }
                randomString = getRandomString(globalState);
            }

            if (randomString.isBlank()) {
                randomString = "DEFAULT STRING";
            }

            return new FelderaVarcharConstant(randomString);
        }
    }

    public static class FelderaCharConstant extends FelderaConstant {
        private final char value;

        public FelderaCharConstant(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + this.value + "'";
        }

        public static FelderaCharConstant getRandom(FelderaGlobalState globalState) {
            char ch = globalState.getRandomly().getAlphabeticChar().charAt(0);
            while (true) {
                if (StandardCharsets.ISO_8859_1.newEncoder().canEncode(ch)) {
                    return new FelderaCharConstant(ch);
                }
            }
        }
    }

    public static class FelderaBooleanConstant extends FelderaConstant {
        private final boolean value;

        public FelderaBooleanConstant(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static FelderaExpression createNullConstant() {
        return new FelderaNullConstant();
    }

    public static FelderaExpression createVarcharConstant(String text) {
        return new FelderaVarcharConstant(text);
    }

    public static FelderaExpression createDoubleConstant(double val) {
        return new FelderaDoubleConstant(val);
    }

    public static FelderaExpression createIntConstant(long val) {
        return new FelderaIntConstant(val);
    }

    public static FelderaExpression createBooleanConstant(boolean val) {
        return new FelderaBooleanConstant(val);
    }
}
