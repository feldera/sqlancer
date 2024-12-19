package sqlancer.feldera.ast;

import java.util.List;

public class FelderaFunctionCall implements FelderaExpression {
    private final FelderaFunction function;
    private final List<FelderaExpression> arguments;
    private boolean blackbox;

    public FelderaFunctionCall(FelderaFunction function, List<FelderaExpression> arguments) {
        this.function = function;
        this.arguments = arguments;
        this.blackbox = false;
    }

    @Override
    public boolean isBlackbox() {
        return blackbox;
    }

    @Override
    public void setBlackbox(boolean blackbox) {
        this.blackbox = blackbox;
        this.arguments.forEach(arg -> arg.setBlackbox(blackbox));
    }

    public List<FelderaExpression> getArguments() {
        return arguments;
    }

    public FelderaFunction getFunction() {
        return function;
    }

    public String getName() {
        return function.getFunctionName();
    }
}
