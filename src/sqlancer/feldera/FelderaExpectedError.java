package sqlancer.feldera;

import sqlancer.common.query.ExpectedErrors;

import java.util.ArrayList;
import java.util.List;

public class FelderaExpectedError {
    private FelderaExpectedError() {
    }

    public static List<String> getExpectedErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("panic message:");
        return errors;
    }

    public static ExpectedErrors expectedErrors() {
        ExpectedErrors res = new ExpectedErrors();
        res.addAll(getExpectedErrors());
        return res;
    }
}
