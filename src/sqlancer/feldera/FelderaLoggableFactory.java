package sqlancer.feldera;

import sqlancer.common.log.Loggable;
import sqlancer.common.log.LoggableFactory;
import sqlancer.common.log.LoggedString;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.common.query.Query;
import sqlancer.feldera.query.FelderaOtherQuery;
import sqlancer.feldera.query.FelderaQueryAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FelderaLoggableFactory extends LoggableFactory {
    @Override
    protected Loggable createLoggable(String input, String suffix) {
        String completeString = input;
        if (!input.endsWith(";")) {
            completeString += ";";
        }
        if (suffix != null && !suffix.isEmpty()) {
            completeString += suffix;
        }
        return new LoggedString(completeString);
    }

    @Override
    public FelderaQueryAdapter getQueryForStateToReproduce(String queryString) {
        return new FelderaOtherQuery(queryString, FelderaExpectedError.expectedErrors());
    }

    @Override
    public FelderaQueryAdapter commentOutQuery(Query<?> query) {
        String queryString = query.getQueryString();
        String newQueryString = "-- " + queryString;
        ExpectedErrors errors = new ExpectedErrors();
        return new FelderaOtherQuery(newQueryString, errors);
    }

    @Override
    protected Loggable infoToLoggable(String time, String pipelineName, String databaseVersion, long seedValue) {
        String sb = "-- Time: " + time + "\n" + "-- Pipeline: " + pipelineName + "\n " + "-- Pipeline version: "
                + databaseVersion + "\n" + "-- seed value: " + seedValue + "\n";
        return new LoggedString(sb);
    }

    @Override
    public Loggable convertStacktraceToLoggable(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return new LoggedString("--" + sw.toString().replace("\n", "\n--"));
    }
}
