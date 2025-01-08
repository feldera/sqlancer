package sqlancer.feldera.ast;

import sqlancer.Randomly;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.FelderaToStringVisitor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class FelderaConstant implements FelderaExpression {
    private FelderaConstant() {
    }

    private static double round(double number, int places) {
        BigDecimal decimal = new BigDecimal(number);
        decimal = decimal.setScale(places, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    public static FelderaExpression getRandomConstant(FelderaGlobalState globalState,
            FelderaSchema.FelderaCompositeDataType type) {
        switch (type.getPrimitiveType()) {
        case BOOLEAN:
            return new FelderaBooleanConstant(Randomly.getBoolean());
        case INT:
            return FelderaIntConstant.getRandom(globalState, type.getSize());
        case VARCHAR:
            return FelderaVarcharConstant.getRandom(globalState, type.getSize());
        case CHAR:
            return FelderaCharConstant.getRandom(globalState, type.getSize());
        case NULL:
            return new FelderaNullConstant();
        case TIME:
            return new FelderaCast(FelderaTimeConstant.getRandom(globalState), type);
        case DATE:
            return new FelderaCast(FelderaDateConstant.getRandom(globalState), type);
        case TIMESTAMP:
            return new FelderaCast(FelderaTimestampConstant.getRandom(globalState), type);
        case FLOAT:
            return FelderaFloatConstant.getRandom(globalState, type.getSize());
        case DECIMAL:
            return FelderaDecimalConstant.getRandom(globalState, type.getSize(), type.getScale());
        case ARRAY:
            return FelderaArrayConstant.getRandom(globalState, type.getElementType());
        default:
            throw new AssertionError(type);
        }
    }

    public static class FelderaArrayConstant extends FelderaConstant {
        private final List<String> array;

        public FelderaArrayConstant(List<String> array) {
            this.array = array;
        }

        public List<String> getValue() {
            return array;
        }

        public static FelderaArrayConstant getRandom(FelderaGlobalState globalState,
                FelderaSchema.FelderaCompositeDataType type) {
            int size = Randomly.fromOptions(1, 2, 3);
            List<String> array = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                FelderaExpression expr = getRandomConstant(globalState, type);
                array.add(FelderaToStringVisitor.asString(expr));
            }

            return new FelderaArrayConstant(array);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("array[");
            for (int i = 0; i < array.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(array.get(i));
            }

            sb.append("]");
            return sb.toString();
        }
    }

    public static class FelderaDecimalConstant extends FelderaConstant {
        private final BigDecimal value;

        public FelderaDecimalConstant(BigDecimal value) {
            this.value = value;
        }

        public BigDecimal getValue() {
            return this.value;
        }

        public static FelderaDecimalConstant getRandom(FelderaGlobalState globalState, int size, int scale) {
            // unsure what to do about size for now
            BigDecimal value = globalState.getRandomly().getRandomBigDecimal().setScale(scale, RoundingMode.HALF_UP)
                    .add(BigDecimal.ONE);

            return new FelderaDecimalConstant(value);
        }

        @Override
        public String toString() {
            return value.toPlainString();
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

    public static class FelderaFloatConstant extends FelderaConstant {
        private final double value;

        public FelderaFloatConstant(double value) {
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

        public static FelderaFloatConstant getRandom(FelderaGlobalState globalState, int size) {
            double value = globalState.getRandomly().getFiniteDouble() + 1.0;
            switch (size) {
            case 32:
                return new FelderaFloatConstant(FelderaConstant.round(value, 5));
            case 64:
                return new FelderaFloatConstant(FelderaConstant.round(value, 10));
            default:
                throw new AssertionError(size);
            }
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

        public static FelderaVarcharConstant getRandom(FelderaGlobalState globalState, int size) {
            if (size < 0) {
                return FelderaVarcharConstant.getRandom(globalState);
            }

            StringBuilder sb = new StringBuilder();
            CharsetEncoder encoder = StandardCharsets.ISO_8859_1.newEncoder();
            while (sb.length() < size) {
                char ch = globalState.getRandomly().getAlphabeticChar().charAt(0);
                if (encoder.canEncode(ch)) {
                    sb.append(ch);
                }
            }

            return new FelderaVarcharConstant(sb.toString());
        }
    }

    public static class FelderaCharConstant extends FelderaConstant {
        private final String value;

        public FelderaCharConstant(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + this.value + "'";
        }

        public static FelderaCharConstant getRandom(FelderaGlobalState globalState, int length) {
            StringBuilder sb = new StringBuilder();
            CharsetEncoder encoder = StandardCharsets.ISO_8859_1.newEncoder();

            for (int i = 0; i < length; i++) {
                for (int attempts = 0; attempts < 10; attempts++) {
                    char ch = globalState.getRandomly().getAlphabeticChar().charAt(0);
                    if (encoder.canEncode(ch)) {
                        sb.append(ch);
                        break;
                    }
                }
                sb.append("x");
            }

            return new FelderaCharConstant(sb.toString());
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

    public static FelderaExpression createFloatConstant(double val) {
        return new FelderaFloatConstant(val);
    }

    public static FelderaExpression createIntConstant(long val) {
        return new FelderaIntConstant(val);
    }

    public static FelderaExpression createBooleanConstant(boolean val) {
        return new FelderaBooleanConstant(val);
    }
}
