package sqlancer.feldera.ast;

import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.feldera.FelderaSchema;

public class FelderaTableReference extends TableReferenceNode<FelderaExpression, FelderaSchema.FelderaTable>
        implements FelderaExpression {
    public FelderaTableReference(FelderaSchema.FelderaTable table) {
        super(table);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FelderaTableReference)) {
            return false;
        }

        FelderaTableReference other = (FelderaTableReference) obj;
        return this.getTable().equals(other.getTable());
    }
}
