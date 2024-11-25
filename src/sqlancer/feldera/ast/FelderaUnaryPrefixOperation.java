package sqlancer.feldera.ast;

import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.newast.NewUnaryPrefixOperatorNode;

public class FelderaUnaryPrefixOperation extends NewUnaryPrefixOperatorNode<FelderaExpression>
        implements FelderaExpression {

    public FelderaUnaryPrefixOperation(FelderaExpression expr, BinaryOperatorNode.Operator op) {
        super(expr, op);
    }

    public enum FelderaUnaryPrefixOperator implements BinaryOperatorNode.Operator {
        NOT("NOT"), UNARY_PLUS("+"), UNARY_MINUS("-");

        private String textRepresentation;

        FelderaUnaryPrefixOperator(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        @Override
        public String getTextRepresentation() {
            return this.textRepresentation;
        }
    }
}
