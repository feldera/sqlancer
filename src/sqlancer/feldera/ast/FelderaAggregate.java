package sqlancer.feldera.ast;

import sqlancer.feldera.FelderaSchema;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FelderaAggregate implements FelderaExpression {
    private final FelderaAggregateFunction func;
    private final List<FelderaExpression> expr;
    private boolean blackbox;

    public enum FelderaAggregateFunction {
        AVG(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.DOUBLE),
        COUNT(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.DOUBLE,
                FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.VARCHAR),
        EVERY(FelderaSchema.FelderaDataType.BOOLEAN),
        MAX(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.INT),
        MIN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.INT),
        SOME(FelderaSchema.FelderaDataType.BOOLEAN),
        SUM(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.DOUBLE),
        STDDEV(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.DOUBLE),
        STDDEV_POP(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.DOUBLE),;

        private final FelderaSchema.FelderaDataType[] supportedReturnTypes;

        FelderaAggregateFunction(FelderaSchema.FelderaDataType... supportedReturnTypes) {
            this.supportedReturnTypes = supportedReturnTypes.clone();
        }

        public List<FelderaSchema.FelderaDataType> getTypes(FelderaSchema.FelderaDataType returnType) {
            return Collections.singletonList(returnType);
        }

        public boolean supportsReturnType(FelderaSchema.FelderaDataType returnType) {
            return Arrays.stream(supportedReturnTypes).anyMatch(t -> t == returnType)
                    || supportedReturnTypes.length == 0;
        }

        public static List<FelderaAggregateFunction> getAggregates(FelderaSchema.FelderaDataType type) {
            return Arrays.stream(values()).filter(p -> p.supportsReturnType(type)).collect(Collectors.toList());
        }
    }

    public FelderaAggregate(FelderaAggregateFunction func, List<FelderaExpression> expr) {
        this.func = func;
        this.expr = expr;
        this.blackbox = false;
    }

    @Override
    public boolean isBlackbox() {
        return blackbox;
    }

    @Override
    public void setBlackbox(boolean blackbox) {
        this.blackbox = blackbox;
        this.expr.forEach(f -> f.setBlackbox(blackbox));
    }

    public FelderaAggregateFunction getFunc() {
        return func;
    }

    public List<FelderaExpression> getExpr() {
        return expr;
    }
}
