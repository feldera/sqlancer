package sqlancer.feldera.ast;

import sqlancer.common.ast.newast.ColumnReferenceNode;
import sqlancer.feldera.FelderaSchema;

public class FelderaColumnReference extends ColumnReferenceNode<FelderaExpression, FelderaSchema.FelderaColumn> implements FelderaExpression {
    public FelderaColumnReference(FelderaSchema.FelderaColumn felderaColumn) {
        super(felderaColumn);
    }
}
