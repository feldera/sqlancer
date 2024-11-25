package sqlancer.feldera.ast;

import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.newast.NewUnaryPostfixOperatorNode;

public class FelderaUnaryPostfixOperation extends NewUnaryPostfixOperatorNode<FelderaExpression>
        implements FelderaExpression {

    public enum FelderaUnaryPostfixOperator implements BinaryOperatorNode.Operator {
        IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL"), IS_FALSE("IS FALSE"), IS_NOT_TRUE("IS NOT TRUE"),
        IS_NOT_FALSE("IS NOT FALSE");

        private String s;

        FelderaUnaryPostfixOperator(String s) {
            this.s = s;
        }

        @Override
        public String getTextRepresentation() {
            return s;
        }
    }

    public FelderaUnaryPostfixOperation(FelderaExpression expr, FelderaUnaryPostfixOperator op) {
        super(expr, op);
    }
}
