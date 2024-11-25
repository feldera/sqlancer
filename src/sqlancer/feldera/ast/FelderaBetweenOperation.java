package sqlancer.feldera.ast;

import sqlancer.Randomly;

public class FelderaBetweenOperation implements FelderaExpression {
    private final FelderaExpression expr;
    private final FelderaExpression left;
    private final FelderaExpression right;
    private FelderaBetweenOperatorType type;

    public enum FelderaBetweenOperatorType {
        BETWEEN("BETWEEN"), NOT_BETWEEN("NOT BETWEEN");

        private String s;

        FelderaBetweenOperatorType(String s) {
            this.s = s;
        }

        public String getStringRepresentation() {
            return s;
        }

        public static FelderaBetweenOperatorType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public FelderaBetweenOperation(FelderaExpression expr, FelderaExpression left, FelderaExpression right,
            FelderaBetweenOperatorType type) {
        this.expr = expr;
        this.left = left;
        this.right = right;
        this.type = type;
    }

    public FelderaExpression getLeft() {
        return left;
    }

    public FelderaExpression getRight() {
        return right;
    }

    public FelderaExpression getExpr() {
        return expr;
    }

    public FelderaBetweenOperatorType getType() {
        return type;
    }
}
