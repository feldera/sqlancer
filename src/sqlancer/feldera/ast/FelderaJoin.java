package sqlancer.feldera.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Join;
import sqlancer.feldera.FelderaSchema;

public class FelderaJoin
        implements FelderaExpression, Join<FelderaExpression, FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn> {

    private final FelderaTableReference leftTable;
    private final FelderaTableReference rightTable;
    private final FelderaJoinType joinType;
    private FelderaExpression onCondition;

    public enum FelderaJoinType {
        INNER, NATURAL, LEFT, RIGHT;

        public static FelderaJoinType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public FelderaJoin(FelderaTableReference leftTable, FelderaTableReference rightTable, FelderaJoinType joinType,
            FelderaExpression whereCondition) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        this.joinType = joinType;
        this.onCondition = whereCondition;
    }

    public FelderaTableReference getLeftTable() {
        return leftTable;
    }

    public FelderaTableReference getRightTable() {
        return rightTable;
    }

    public FelderaJoinType getJoinType() {
        return joinType;
    }

    public FelderaExpression getOnCondition() {
        return onCondition;
    }

    @Override
    public void setOnClause(FelderaExpression onClause) {
        this.onCondition = onClause;
    }
}
