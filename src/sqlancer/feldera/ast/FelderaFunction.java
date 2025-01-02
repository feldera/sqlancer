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

    // Float
    ABS(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    CEIL(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    FLOOR(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    TRUNCATE1("TRUNCATE", FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    // TRUNCATE2("TRUNCATE", FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT,
    // FelderaSchema.FelderaDataType.INT),
    ROUND1("ROUND", FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    // ROUND2("ROUND", FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT,
    // FelderaSchema.FelderaDataType.INT),
    POWER(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT,
            FelderaSchema.FelderaDataType.FLOAT),
    POWER2("POWER", FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.INT,
            FelderaSchema.FelderaDataType.INT),
    SQRT(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    EXP(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    LN(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    LOG(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    LOG10(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    IS_INF(FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.FLOAT),
    IS_NAN(FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.FLOAT),
    SIN(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    COS(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    TAN(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    COT(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    SEC(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    CSC(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ASIN(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ACOS(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ATAN(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ATAN2(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT,
            FelderaSchema.FelderaDataType.FLOAT),
    DEGREES(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    RADIANS(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    CBRT(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    SINH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    COSH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    TANH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    COTH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    SECH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    CSCH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ASINH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ACOSH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),
    ATANH(FelderaSchema.FelderaDataType.FLOAT, FelderaSchema.FelderaDataType.FLOAT),

    // Decimal
    ABS_DECIMAL("ABS", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    CEIL_DECIMAL("CEIL", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    FLOOR_DECIMAL("FLOOR", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    TRUNCATE1_DECIMAL("TRUNCATE", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    // TRUNCATE2_DECIMAL("TRUNCATE", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL,
    // FelderaSchema.FelderaDataType.INT),
    ROUND1_DECIMAL("ROUND", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    // ROUND2_DECIMAL("ROUND", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL,
    // FelderaSchema.FelderaDataType.INT),
    POWER_DECIMAL("POWER", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL,
            FelderaSchema.FelderaDataType.DECIMAL),
    POWER2_DECIMAL("POWER", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.INT,
            FelderaSchema.FelderaDataType.INT),
    SQRT_DECIMAL("SQRT", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    EXP_DECIMAL("EXP", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    LN_DECIMAL("LN", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    LOG_DECIMAL("LOG", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    LOG10_DECIMAL("LOG10", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    IS_INF_DECIMAL("IS_INF", FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.DECIMAL),
    IS_NAN_DECIMAL("IS_NAN", FelderaSchema.FelderaDataType.BOOLEAN, FelderaSchema.FelderaDataType.DECIMAL),
    SIN_DECIMAL("SIN", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    COS_DECIMAL("COS", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    TAN_DECIMAL("TAN", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    COT_DECIMAL("COT", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    SEC_DECIMAL("SEC", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    CSC_DECIMAL("CSC", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ASIN_DECIMAL("ASIN", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ACOS_DECIMAL("ACOS", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ATAN_DECIMAL("ATAN", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ATAN2_DECIMAL("ATAN2", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL,
            FelderaSchema.FelderaDataType.DECIMAL),
    DEGREES_DECIMAL("DEGREES", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    RADIANS_DECIMAL("RADIANS", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    CBRT_DECIMAL("CBRT", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    SINH_DECIMAL("SINH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    COSH_DECIMAL("COSH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    TANH_DECIMAL("TANH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    COTH_DECIMAL("COTH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    SECH_DECIMAL("SECH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    CSCH_DECIMAL("CSCH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ASINH_DECIMAL("ASINH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ACOSH_DECIMAL("ACOSH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),
    ATANH_DECIMAL("ATANH", FelderaSchema.FelderaDataType.DECIMAL, FelderaSchema.FelderaDataType.DECIMAL),

    // Int
    ABS_INT("ABS", FelderaSchema.FelderaDataType.INT, FelderaSchema.FelderaDataType.INT),
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

    public boolean isCompatibleWithReturnType(FelderaSchema.FelderaCompositeDataType dataType) {
        return this.returnType == dataType.getPrimitiveType();
    }

    public String getFunctionName() {
        return functionName;
    }

    public FelderaSchema.FelderaDataType[] getArgumentTypes() {
        return argumentTypes;
    }

    public FelderaFunctionCall getCall(FelderaSchema.FelderaCompositeDataType returnType,
            FelderaExpressionGenerator gen, int depth) {
        FelderaSchema.FelderaDataType[] argumentTypes = getArgumentTypes();
        List<FelderaExpression> arguments = getArgumentsForReturnType(gen, depth, argumentTypes, returnType);
        return new FelderaFunctionCall(this, arguments);
    }

    List<FelderaExpression> getArgumentsForReturnType(FelderaExpressionGenerator gen, int depth,
            FelderaSchema.FelderaDataType[] argumentTypes, FelderaSchema.FelderaCompositeDataType returnType) {
        List<FelderaExpression> arguments = new ArrayList<>();

        for (FelderaSchema.FelderaDataType arg : argumentTypes) {
            arguments.add(gen.generateExpression(FelderaSchema.FelderaCompositeDataType.getRandomFromPrimitiveType(arg),
                    depth + 1));
        }

        return arguments;
    }

    public static List<FelderaFunction> getFunctionCompatibleWith(FelderaSchema.FelderaCompositeDataType returnType) {
        return Stream.of(values()).filter(f -> f.isCompatibleWithReturnType(returnType)).collect(Collectors.toList());
    }
}
