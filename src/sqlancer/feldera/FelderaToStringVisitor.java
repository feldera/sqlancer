package sqlancer.feldera;

import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.feldera.ast.FelderaConstant;
import sqlancer.feldera.ast.FelderaExpression;

public class FelderaToStringVisitor extends NewToStringVisitor<FelderaExpression> {
    @Override
    public void visitSpecific(FelderaExpression expr) {
        if (expr instanceof FelderaConstant) {
            visit((FelderaConstant) expr);
        } else {
            throw new AssertionError(expr.getClass());
        }
    }

    private void visit(FelderaConstant constant) {
        sb.append(constant.toString());
    }

    public static String asString(FelderaExpression expr) {
        FelderaToStringVisitor visitor = new FelderaToStringVisitor();
        visitor.visit(expr);
        return visitor.get();
    }
}
