package sqlancer.feldera.ast;

import sqlancer.common.ast.newast.Expression;
import sqlancer.feldera.FelderaSchema;

public interface FelderaExpression extends Expression<FelderaSchema.FelderaColumn> {
    default public boolean isBlackbox() {
        return false;
    }

    default public void setBlackbox(boolean blackbox) {
    }
}
