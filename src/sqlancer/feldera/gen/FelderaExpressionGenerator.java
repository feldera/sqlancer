package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.gen.NoRECGenerator;
import sqlancer.common.gen.TypedExpressionGenerator;
import sqlancer.common.schema.AbstractTables;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.ast.*;

import java.util.*;
import java.util.stream.Collectors;

public final class FelderaExpressionGenerator
        extends TypedExpressionGenerator<FelderaExpression, FelderaSchema.FelderaColumn, FelderaSchema.FelderaDataType>
        implements
        NoRECGenerator<FelderaSelect, FelderaJoin, FelderaExpression, FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn> {

    private List<FelderaSchema.FelderaTable> tables;
    private final FelderaGlobalState globalState;
    private final int maxDepth;

    public FelderaExpressionGenerator(FelderaGlobalState globalState) {
        this.globalState = globalState;
        this.maxDepth = globalState.getOptions().getMaxExpressionDepth();
    }

    private enum BooleanExpression {
        NOT, COMPARISON, IS_NULL, BETWEEN;
    }

    private FelderaExpression getBinaryComparison(int depth) {
        FelderaSchema.FelderaDataType type = getRandomType();
        FelderaExpression left = generateExpression(type, depth + 1);
        FelderaExpression right = generateExpression(type, depth + 1);
        return new FelderaBinaryComparisonOperation(left, right,
                FelderaBinaryComparisonOperation.FelderaComparisonOperator.getRandom());
    }

    private FelderaExpression generateBooleanExpression(int depth) {
        BooleanExpression exprType = Randomly.fromOptions(BooleanExpression.values());
        FelderaExpression expr;

        switch (exprType) {
        case NOT:
            return new FelderaUnaryPrefixOperation(generateExpression(FelderaSchema.FelderaDataType.BOOLEAN, depth + 1),
                    FelderaUnaryPrefixOperation.FelderaUnaryPrefixOperator.NOT);
        case COMPARISON:
            return getBinaryComparison(depth);
        case IS_NULL:
            return new FelderaUnaryPostfixOperation(generateExpression(getRandomType(), depth + 1),
                    Randomly.fromOptions(FelderaUnaryPostfixOperation.FelderaUnaryPostfixOperator.IS_NULL,
                            FelderaUnaryPostfixOperation.FelderaUnaryPostfixOperator.IS_NOT_NULL));
        case BETWEEN:
            FelderaSchema.FelderaDataType type = getRandomType();
            expr = generateExpression(type, depth + 1);
            FelderaExpression left = generateExpression(type, depth + 1);
            FelderaExpression right = generateExpression(type, depth + 1);
            return new FelderaBetweenOperation(expr, left, right,
                    FelderaBetweenOperation.FelderaBetweenOperatorType.getRandom());
        default:
            throw new AssertionError(exprType);
        }
    }

    private FelderaExpression generateStringExpression(int depth) {
        FelderaSchema.FelderaDataType type = FelderaSchema.FelderaDataType.VARCHAR;
        List<FelderaFunction> applicableFunctions = FelderaFunction.getFunctionCompatibleWith(type);
        if (!applicableFunctions.isEmpty()) {
            FelderaFunction function = Randomly.fromList(applicableFunctions);
            return function.getCall(type, this, depth + 1);
        }

        return generateLeafNode(FelderaSchema.FelderaDataType.VARCHAR);
    }

    private FelderaExpression generateIntegerString() {
        String s = Randomly.StringGenerationStrategy.NUMERIC.getString(globalState.getRandomly());
        if (s.isBlank()) {
            s = "1";
        }
        return new FelderaConstant.FelderaVarcharConstant(s);
    }

    private FelderaExpression getBinaryArithmeticOperation(FelderaSchema.FelderaDataType type, int depth) {
        if (Randomly.getBoolean()) {
            type = FelderaSchema.FelderaDataType.getRandomNumericType();
        }
        return new FelderaBinaryArithmeticOperation(generateExpression(type, depth + 1),
                generateExpression(type, depth + 1),
                FelderaBinaryArithmeticOperation.FelderaBinaryArithmeticOperator.getRandom());
    }

    @Override
    protected FelderaSchema.FelderaDataType getRandomType() {
        return FelderaSchema.FelderaDataType.getRandomNonNullType();
    }

    @Override
    protected boolean canGenerateColumnOfType(FelderaSchema.FelderaDataType type) {
        List<FelderaSchema.FelderaColumn> columns = filterColumns(type);
        return !columns.isEmpty();
    }

    private FelderaExpression getAggregate(FelderaSchema.FelderaDataType type) throws IndexOutOfBoundsException {
        FelderaAggregate.FelderaAggregateFunction agg = Randomly
                .fromList(FelderaAggregate.FelderaAggregateFunction.getAggregates(type));
        return generateArgsForAggregate(type, agg);
    }

    public FelderaExpression generateAggregate() {
        while (true) {
            try {
                return getAggregate(getRandomType());
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    private FelderaAggregate generateArgsForAggregate(FelderaSchema.FelderaDataType type,
            FelderaAggregate.FelderaAggregateFunction agg) {
        List<FelderaSchema.FelderaDataType> types = agg.getTypes(type);
        List<FelderaExpression> args = new ArrayList<>();
        allowAggregates = false;
        for (FelderaSchema.FelderaDataType argType : types) {
            args.add(generateExpression(argType));
        }

        return new FelderaAggregate(agg, args);

    }

    @Override
    public FelderaExpression generateExpression(FelderaSchema.FelderaDataType type, int depth) {
        if (depth >= maxDepth) {
            return generateLeafNode(type);
        }

        if (allowAggregates && Randomly.getBoolean()
                && !FelderaAggregate.FelderaAggregateFunction.getAggregates(type).isEmpty()) {
            return getAggregate(type);
        }

        if (Randomly.getBoolean()) {
            List<FelderaFunction> applicableFunctions = FelderaFunction.getFunctionCompatibleWith(type);
            if (!applicableFunctions.isEmpty()) {
                FelderaFunction function = Randomly.fromList(applicableFunctions);
                return function.getCall(type, this, depth + 1);
            }
        }
        if (type.isNumeric() && Randomly.getBooleanWithSmallProbability()) {
            FelderaSchema.FelderaDataType randomType = FelderaSchema.FelderaDataType.getRandomType();
            FelderaExpression expr;
            if (randomType == FelderaSchema.FelderaDataType.VARCHAR) {
                expr = generateIntegerString();
            } else if (!randomType.isNumeric()) {
                expr = generateExpression(FelderaSchema.FelderaDataType.getRandomNumericType(), depth + 1);
            } else {
                expr = generateExpression(randomType, depth + 1);
            }
            return new FelderaCast(expr, type);
        }

        switch (type) {
        case BOOLEAN:
            return generateBooleanExpression(depth);
        case VARCHAR:
            return generateStringExpression(depth);
        case TINYINT:
        case SMALLINT:
        case INT:
        case BIGINT:
        case REAL:
        case DOUBLE:
            return getBinaryArithmeticOperation(type, depth);
        case DATE:
        case TIMESTAMP:
        case TIME:
        case CHAR:
            FelderaExpression expr = FelderaConstant.getRandomConstant(globalState, type);
            return new FelderaCast(expr, type);
        default:
            throw new AssertionError(type);
        }
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
        return new FelderaUnaryPrefixOperation(predicate, FelderaUnaryPrefixOperation.FelderaUnaryPrefixOperator.NOT);
    }

    @Override
    public FelderaExpression isNull(FelderaExpression expr) {
        return new FelderaUnaryPostfixOperation(expr, FelderaUnaryPostfixOperation.FelderaUnaryPostfixOperator.IS_NULL);
    }

    @Override
    public FelderaExpressionGenerator setTablesAndColumns(
            AbstractTables<FelderaSchema.FelderaTable, FelderaSchema.FelderaColumn> tables) {
        this.tables = tables.getTables();
        this.columns = tables.getColumns();

        return this;
    }

    @Override
    public FelderaExpression generateBooleanExpression() {
        return generateExpression(FelderaSchema.FelderaDataType.BOOLEAN);
    }

    @Override
    public FelderaSelect generateSelect() {
        FelderaSelect select = new FelderaSelect();
        select.setDistinct(Randomly.getBooleanWithSmallProbability());

        List<FelderaExpression> columns = new ArrayList<>();

        int nrColumns = Randomly.smallNumber() + 1;

        for (int i = 0; i < nrColumns; i++) {
            if (allowAggregates && Randomly.getBoolean()) {
                FelderaExpression expression = generateExpression(getRandomType());
                columns.add(expression);
            } else {
                columns.add(generateAggregate());
            }
        }

        select.setFetchColumns(columns);
        List<FelderaJoin> randomJoins = getRandomJoinClauses();
        List<FelderaTableReference> joinedTables = new ArrayList<>();

        for (FelderaJoin join : randomJoins) {
            joinedTables.add(join.getLeftTable());
            joinedTables.add(join.getRightTable());
        }

        select.setJoinClauses(randomJoins);

        List<FelderaExpression> fromList = getTableRefs().stream()
                .filter(t -> !joinedTables.contains((FelderaTableReference) t)).collect(Collectors.toList());
        select.setFromList(fromList);

        return select;
    }

    @Override
    public List<FelderaJoin> getRandomJoinClauses() {
        return getRandomJoinClauses(new ArrayList<>(tables));
    }

    public List<FelderaJoin> getRandomJoinClauses(List<FelderaSchema.FelderaTable> tables) {
        List<FelderaTableReference> tablesRef = tables.stream().map(FelderaTableReference::new)
                .collect(Collectors.toList());
        List<FelderaJoin> joinStatements = new ArrayList<>();
        List<FelderaJoin.FelderaJoinType> options = new ArrayList<>(
                Arrays.asList(FelderaJoin.FelderaJoinType.values()));

        if (tablesRef.size() >= 2) {
            int nrJoinClauses = (int) Randomly.getNotCachedInteger(0, (tables.size() / 2) - 1);
            // natural join is incompatible with other joins
            // because it needs unique column names
            // while other joins will produce duplicate column names
            if (nrJoinClauses > 1) {
                options.remove(FelderaJoin.FelderaJoinType.NATURAL);
            }
            for (int i = 0; i < nrJoinClauses; i++) {
                FelderaExpression joinClause = generatePredicate();
                FelderaTableReference leftTable = Randomly.fromList(tablesRef);
                tablesRef.remove(leftTable);
                FelderaTableReference rightTable = Randomly.fromList(tablesRef);
                tablesRef.remove(rightTable);

                FelderaJoin.FelderaJoinType selectedOption = Randomly.fromList(options);
                if (selectedOption == FelderaJoin.FelderaJoinType.NATURAL) {
                    // NATURAL joins do not have an ON clause
                    joinClause = null;
                }

                FelderaJoin j = new FelderaJoin(leftTable, rightTable, selectedOption, joinClause);
                joinStatements.add(j);
            }
        }

        return joinStatements;
    }

    @Override
    public List<FelderaExpression> getTableRefs() {
        return tables.stream().map(FelderaTableReference::new).collect(Collectors.toList());
    }

    @Override
    public String generateOptimizedQueryString(FelderaSelect select, FelderaExpression whereCondition,
            boolean shouldUseAggregate) {
        select.setWhereClause(whereCondition);

        return select.asString();
    }

    @Override
    public String generateUnoptimizedQueryString(FelderaSelect select, FelderaExpression whereCondition) {
        whereCondition.setBlackbox(true);
        select.getFetchColumns().forEach(c -> {
            c.setBlackbox(true);
        });
        select.setWhereClause(whereCondition);

        return select.asString();
    }
}