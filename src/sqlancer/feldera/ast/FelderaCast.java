package sqlancer.feldera.ast;

import sqlancer.feldera.FelderaSchema;

public class FelderaCast implements FelderaExpression {
    private final FelderaExpression expr;
    private final FelderaSchema.FelderaCompositeDataType type;

    public FelderaCast(FelderaExpression expr, FelderaSchema.FelderaCompositeDataType type) {
        this.expr = expr;
        this.type = type;
    }

    public FelderaExpression getExpression() {
        return expr;
    }

    public String getStringRepresentation() {
        return " AS " + type.toString();
    }

}