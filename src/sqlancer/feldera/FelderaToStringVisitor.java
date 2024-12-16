package sqlancer.feldera;

import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.feldera.ast.*;

public class FelderaToStringVisitor extends NewToStringVisitor<FelderaExpression> {
    @Override
    public void visitSpecific(FelderaExpression expr) {
        if (expr.isBlackbox()) {
            sb.append("blackbox(");
        }
        if (expr instanceof FelderaConstant) {
            visit((FelderaConstant) expr);
        } else if (expr instanceof FelderaSelect) {
            visit((FelderaSelect) expr);
        } else if (expr instanceof FelderaJoin) {
            visit((FelderaJoin) expr);
        } else if (expr instanceof FelderaCast) {
            visit((FelderaCast) expr);
        } else if (expr instanceof FelderaBetweenOperation) {
            visit((FelderaBetweenOperation) expr);
        } else if (expr instanceof FelderaFunctionCall) {
            visit((FelderaFunctionCall) expr);
        } else if (expr instanceof FelderaAggregate) {
            visit((FelderaAggregate) expr);
        } else {
            throw new AssertionError(expr.toString());
        }
        if (expr.isBlackbox()) {
            sb.append(")");
        }
    }

    private void visit(FelderaAggregate aggr) {
        sb.append(aggr.getFunc().name());
        sb.append("(");
        visit(aggr.getExpr());
        sb.append(")");
    }

    private void visit(FelderaCast cast) {
        sb.append("CAST(");
        visit(cast.getExpression());
        sb.append(cast.getStringRepresentation());
        sb.append(")");
    }

    private void visit(FelderaBetweenOperation op) {
        sb.append("(");
        visit(op.getExpr());
        sb.append(")");
        sb.append(" ");
        sb.append(op.getType().getStringRepresentation());
        sb.append(" (");
        visit(op.getLeft());
        sb.append(") AND (");
        visit(op.getRight());
        sb.append(")");
    }

    private void visit(FelderaFunctionCall call) {
        sb.append(call.getName());
        sb.append("(");
        visit(call.getArguments());
        sb.append(")");
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
        visit(select.getFetchColumns());
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
            sb.append(" ORDER BY ");
            visit(select.getOrderByClauses());
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
