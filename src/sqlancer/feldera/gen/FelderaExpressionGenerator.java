package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.gen.NoRECGenerator;
import sqlancer.common.gen.TypedExpressionGenerator;
import sqlancer.common.schema.AbstractTables;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.FelderaToStringVisitor;
import sqlancer.feldera.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FelderaExpressionGenerator
        extends TypedExpressionGenerator<FelderaExpression, FelderaSchema.FelderaColumn, FelderaSchema.FelderaDataType>
    implements NoRECGenerator<FelderaSelect, FelderaJoin, FelderaExpression, FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn>
{

    @SuppressWarnings("unused")
    private List<FelderaSchema.FelderaTable> tables;
    private final FelderaGlobalState globalState;
    @SuppressWarnings("unused")
    private final int maxDepth;

    public FelderaExpressionGenerator(FelderaGlobalState globalState) {
        this.globalState = globalState;
        this.maxDepth = globalState.getOptions().getMaxExpressionDepth();
    }

    @Override
    protected FelderaSchema.FelderaDataType getRandomType() {
        return FelderaSchema.FelderaDataType.getRandomType();
    }

    @Override
    protected boolean canGenerateColumnOfType(FelderaSchema.FelderaDataType type) {
        return true;
    }


    @Override
    protected FelderaExpression generateExpression(FelderaSchema.FelderaDataType type, int depth) {
        return generateLeafNode(type);
    }

    List<FelderaSchema.FelderaColumn> filterColumns(FelderaSchema.FelderaDataType type) {
        if (columns == null) {
            return Collections.emptyList();
        } else {
            return columns.stream().filter(c -> c.getType() == type).collect(Collectors.toList());
        }
    }

    @Override
    protected FelderaExpression generateColumn(FelderaSchema.FelderaDataType type) {
        // HACK: if no col of such type exists, generate constant value instead
        List<FelderaSchema.FelderaColumn> colsOfType = filterColumns(type);
        if (colsOfType.isEmpty()) {
            return generateConstant(type);
        }

        FelderaSchema.FelderaColumn column = Randomly.fromList(colsOfType);
        return new FelderaColumnReference(column);
    }


    @Override
    public FelderaExpression generateConstant(FelderaSchema.FelderaDataType type) {
        return type.getRandomConstant(globalState);
    }

    @Override
    public FelderaExpression generatePredicate() {
        return generateExpression(FelderaSchema.FelderaDataType.BOOLEAN, 0);
    }

    @Override
    public FelderaExpression negatePredicate(FelderaExpression predicate) {
        return null;
    }

    @Override
    public FelderaExpression isNull(FelderaExpression expr) {
        return null;
    }

    @Override
    public FelderaExpressionGenerator setTablesAndColumns(AbstractTables<FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn> tables) {
        List<FelderaSchema.FelderaTable> randomTables = Randomly.nonEmptySubset(tables.getTables());
        int maxSize = Randomly.fromOptions(1, 2, 3, 4);
        if (randomTables.size() > maxSize) {
            randomTables = randomTables.subList(0, maxSize);
        }
        this.columns = FelderaSchema.FelderaTable.getAllColumns(randomTables);
        this.tables = randomTables;

        return this;
    }

    @Override
    public FelderaExpression generateBooleanExpression() {
        return generateExpression(FelderaSchema.FelderaDataType.BOOLEAN);
    }

    @Override
    public FelderaSelect generateSelect() {
        return new FelderaSelect();
    }

    @Override
    public List<FelderaJoin> getRandomJoinClauses() {
        return new ArrayList<>();
    }

    @Override
    public List<FelderaExpression> getTableRefs() {
        return null;
    }

    @Override
    public String generateOptimizedQueryString(FelderaSelect select, FelderaExpression whereCondition, boolean shouldUseAggregate) {
        select.setFetchColumnString("COUNT(*)");
        select.setWhereClause(whereCondition);

        return select.asString();
    }

    @Override
    public String generateUnoptimizedQueryString(FelderaSelect select, FelderaExpression whereCondition) {
        String fetchColumn = String.format("COUNT((CASE WHEN %S THEN 1 ELSE NULL END))",
                FelderaToStringVisitor.asString(whereCondition));
        select.setFetchColumnString(fetchColumn);
        select.setWhereClause(null);

        return select.asString();
    }
}