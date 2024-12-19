package sqlancer.feldera.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.newast.NewBinaryOperatorNode;

public class FelderaBinaryComparisonOperation extends NewBinaryOperatorNode<FelderaExpression>
        implements FelderaExpression {

    public enum FelderaComparisonOperator implements BinaryOperatorNode.Operator {
        EQUALS("="), GREATER(">"), GREATER_EQUALS(">="), SMALLER("<"), SMALLER_EQUALS("<="), NOT_EQUALS("!="),
        IS_DISTINCT_FROM("IS DISTINCT FROM"), IS_NOT_DISTINCT_FROM("IS NOT DISTINCT FROM");

        private String textRepr;

        FelderaComparisonOperator(String textRepr) {
            this.textRepr = textRepr;
        }

        public static FelderaComparisonOperator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return textRepr;
        }
    }

    public FelderaBinaryComparisonOperation(FelderaExpression left, FelderaExpression right,
            FelderaComparisonOperator op) {
        super(left, right, op);
    }
}