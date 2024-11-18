package sqlancer.feldera;

import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.feldera.ast.*;

public class FelderaToStringVisitor extends NewToStringVisitor<FelderaExpression> {
    @Override
    public void visitSpecific(FelderaExpression expr) {
        if (expr instanceof FelderaConstant) {
            visit((FelderaConstant) expr);
        } else if (expr instanceof FelderaSelect) {
            visit((FelderaSelect) expr);
        } else if (expr instanceof FelderaJoin) {
            visit((FelderaJoin) expr);
        } else {
            throw new AssertionError(expr.toString());
        }
    }

    public void visit(FelderaJoin join) {
        visit((TableReferenceNode<FelderaExpression, FelderaSchema.FelderaTable>) join.getLeftTable());
        sb.append(" ");
        sb.append(join.getJoinType());
        sb.append(" JOIN ");
        visit((TableReferenceNode<FelderaExpression, FelderaSchema.FelderaTable>) join.getRightTable());
        if (join.getOnCondition() != null) {
            sb.append(" ON ");
            visit(join.getOnCondition());
        }
    }

    private void visit(FelderaConstant constant) {
        sb.append(constant.toString());
    }

    private void visit(FelderaSelect select) {
        sb.append("SELECT ");
        if (select.isDistinct()) {
            sb.append("DISTINCT ");
        }
        if (select.fetchColumnString.isPresent()) {
            sb.append(select.fetchColumnString.get());
        } else {
            visit(select.getFetchColumns());
        }
        sb.append(" FROM ");
        visit(select.getFromList());
        if (!select.getFromList().isEmpty() && !select.getJoinList().isEmpty()) {
            sb.append(", ");
        }
        if (!select.getJoinList().isEmpty()) {
            visit(select.getJoinList());
        }
        if (select.getWhereClause() != null) {
            sb.append(" WHERE ");
            visit(select.getWhereClause());
        }
        if (!select.getGroupByExpressions().isEmpty()) {
            sb.append(" GROUP BY ");
            visit(select.getGroupByExpressions());
        }
        if (select.getHavingClause() != null) {
            sb.append(" HAVING ");
            visit(select.getHavingClause());
        }
        if (!select.getOrderByClauses().isEmpty()) {
            sb.append(" LIMIT ");
            visit(select.getOrderByClauses());
        }
        if (select.getOffsetClause() != null) {
            sb.append(" OFFSET ");
            visit(select.getOffsetClause());
        }
    }

    public static String asString(FelderaExpression expr) {
        FelderaToStringVisitor visitor = new FelderaToStringVisitor();
        visitor.visit(expr);
        return visitor.get();
    }
}
