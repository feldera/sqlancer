package sqlancer.feldera.ast;

import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.gen.FelderaExpressionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FelderaFunction {
    // String
    ASCII(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.VARCHAR),
    CHR(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.INT),
    CONCAT(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR,
            FelderaSchema.FelderaDataType.VARCHAR),
    INITCAP(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR),
    LEFT(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR,
            FelderaSchema.FelderaDataType.INT),
    LOWER(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR),
    REPEAT(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR,
            FelderaSchema.FelderaDataType.INT),
    RLIKE(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR,
            FelderaSchema.FelderaDataType.VARCHAR),
    SUBSTRING(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR,
            FelderaSchema.FelderaDataType.INT),
    TRIM(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR),
    UPPER(FelderaSchema.FelderaDataType.VARCHAR, FelderaSchema.FelderaDataType.VARCHAR),

    // Double
    ABS(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    CEIL(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    FLOOR(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    TRUNCATE1("TRUNCATE", FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    // TRUNCATE2("TRUNCATE", FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE,
    // FelderaSchema.FelderaDataType.INT),
    ROUND1("ROUND", FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    // ROUND2("ROUND", FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE,
    // FelderaSchema.FelderaDataType.INT),
    POWER(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE,
            FelderaSchema.FelderaDataType.DOUBLE),
    POWER2("POWER", FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.INT,
            FelderaSchema.FelderaDataType.INT),
    SQRT(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    EXP(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    LN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    LOG(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    LOG10(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    IS_INF(FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.DOUBLE),
    IS_NAN(FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.DOUBLE),
    SIN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    COS(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    TAN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    COT(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    SEC(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    CSC(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ASIN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ACOS(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ATAN(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ATAN2(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE,
            FelderaSchema.FelderaDataType.DOUBLE),
    DEGREES(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    RADIANS(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    CBRT(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    SINH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    COSH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    TANH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    COTH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    SECH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    CSCH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ASINH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ACOSH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),
    ATANH(FelderaSchema.FelderaDataType.DOUBLE, FelderaSchema.FelderaDataType.DOUBLE),

    // Int
    ABS_INT("abs", FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.INT),
    MOD(FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.INT),;

    private FelderaSchema.FelderaDataType returnType;
    private FelderaSchema.FelderaDataType[] argumentTypes;
    private String functionName;

    FelderaFunction(FelderaSchema.FelderaDataType returnType, FelderaSchema.FelderaDataType... argumentTypes) {
        this.returnType = returnType;
        this.argumentTypes = argumentTypes.clone();
        this.functionName = toString();
    }

    FelderaFunction(FelderaSchema.FelderaDataType returnType) {
        this.returnType = returnType;
        this.argumentTypes = new FelderaSchema.FelderaDataType[0];
        this.functionName = toString();
    }

    FelderaFunction(String functionName, FelderaSchema.FelderaDataType returnType,
            FelderaSchema.FelderaDataType... argumentTypes) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.argumentTypes = argumentTypes.clone();
    }

    public boolean isCompatibleWithReturnType(FelderaSchema.FelderaDataType dataType) {
        return this.returnType == dataType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public FelderaSchema.FelderaDataType[] getArgumentTypes() {
        return argumentTypes;
    }

    public FelderaFunctionCall getCall(FelderaSchema.FelderaDataType returnType, FelderaExpressionGenerator gen,
            int depth) {
        FelderaSchema.FelderaDataType[] argumentTypes = getArgumentTypes();
        List<FelderaExpression> arguments = getArgumentsForReturnType(gen, depth, argumentTypes, returnType);
        return new FelderaFunctionCall(this, arguments);
    }

    List<FelderaExpression> getArgumentsForReturnType(FelderaExpressionGenerator gen, int depth,
            FelderaSchema.FelderaDataType[] argumentTypes, FelderaSchema.FelderaDataType returnType) {
        List<FelderaExpression> arguments = new ArrayList<>();

        for (FelderaSchema.FelderaDataType arg : argumentTypes) {
            arguments.add(gen.generateExpression(arg, depth + 1));
        }

        return arguments;
    }

    public static List<FelderaFunction> getFunctionCompatibleWith(FelderaSchema.FelderaDataType returnType) {
        return Stream.of(values()).filter(f -> f.isCompatibleWithReturnType(returnType)).collect(Collectors.toList());
    }
}
