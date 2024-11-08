package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.common.gen.TypedExpressionGenerator;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.ast.FelderaConstant;
import sqlancer.feldera.ast.FelderaExpression;

public class FelderaExpressionGenerator extends TypedExpressionGenerator<FelderaExpression, FelderaSchema.FelderaColumn, FelderaSchema.FelderaDataType> {

    private final FelderaGlobalState globalState;

    public FelderaExpressionGenerator(FelderaGlobalState globalState) {
        this.globalState = globalState;
    }

    @Override
    public FelderaExpression generatePredicate() {
        return null;
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
    public FelderaExpression generateConstant(FelderaSchema.FelderaDataType type) {
        // TODO support NULL constants
//        if (Randomly.getBooleanWithSmallProbability()) {
//            return FelderaConstant.createNullConstant();
//        }
        switch (type) {
            case INT:
                return FelderaConstant.createIntConstant(globalState.getRandomly().getInteger());
            case DOUBLE:
                // TODO: support infinite doubles
                return FelderaConstant.createDoubleConstant(globalState.getRandomly().getFiniteDouble());
            case VARCHAR:
                return FelderaConstant.createVarcharConstant(globalState.getRandomly().getString());
            case BOOLEAN:
                return FelderaConstant.createBooleanConstant(Randomly.getBoolean());
            default:
                throw new AssertionError(type);
        }
    }

    @Override
    protected FelderaExpression generateExpression(FelderaSchema.FelderaDataType type, int depth) {
        return null;
    }

    @Override
    protected FelderaExpression generateColumn(FelderaSchema.FelderaDataType type) {
        return null;
    }

    @Override
    protected FelderaSchema.FelderaDataType getRandomType() {
        return null;
    }

    @Override
    protected boolean canGenerateColumnOfType(FelderaSchema.FelderaDataType type) {
        return false;
    }
}