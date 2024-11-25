package sqlancer.feldera.ast;

import sqlancer.common.ast.SelectBase;
import sqlancer.common.ast.newast.Select;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.FelderaToStringVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class FelderaSelect extends SelectBase<FelderaExpression>
        implements Select<FelderaJoin, FelderaExpression, FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn>,
        FelderaExpression {
    private boolean isDistinct;

    public void setDistinct(boolean isDistinct) {
        this.isDistinct = isDistinct;
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    @Override
    public void setJoinClauses(List<FelderaJoin> joinStatements) {
        List<FelderaExpression> expressions = joinStatements.stream().map(e -> (FelderaExpression) e)
                .collect(Collectors.toList());
        setJoinList(expressions);
    }

    @Override
    public List<FelderaJoin> getJoinClauses() {
        return getJoinList().stream().map(e -> (FelderaJoin) e).collect(Collectors.toList());
    }

    @Override
    public String asString() {
        return FelderaToStringVisitor.asString(this);
    }
}