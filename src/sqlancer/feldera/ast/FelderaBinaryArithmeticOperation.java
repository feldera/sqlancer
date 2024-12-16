package sqlancer.feldera.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.newast.NewBinaryOperatorNode;

public class FelderaBinaryArithmeticOperation extends NewBinaryOperatorNode<FelderaExpression>
        implements FelderaExpression {

    public enum FelderaBinaryArithmeticOperator implements BinaryOperatorNode.Operator {
        ADD("+"), MULT("*"), MINUS("-"), DIV("/"), MOD("%");

        String textRepresentation;

        FelderaBinaryArithmeticOperator(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        public static FelderaBinaryArithmeticOperator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return textRepresentation;
        }
    }

    public FelderaBinaryArithmeticOperation(FelderaExpression left, FelderaExpression right,
            FelderaBinaryArithmeticOperator op) {
        super(left, right, op);
    }

}
