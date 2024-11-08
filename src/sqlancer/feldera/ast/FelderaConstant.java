package sqlancer.feldera.ast;

public abstract class FelderaConstant implements FelderaExpression {
    private FelderaConstant() {}

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
