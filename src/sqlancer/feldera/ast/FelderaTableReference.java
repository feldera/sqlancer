package sqlancer.feldera.ast;

import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.feldera.FelderaSchema;

public class FelderaTableReference extends TableReferenceNode<FelderaExpression, FelderaSchema.FelderaTable> implements FelderaExpression {
    public FelderaTableReference(FelderaSchema.FelderaTable table) {
        super(table);
    }

}
