package sqlancer.feldera;

import sqlancer.*;

import com.google.auto.service.AutoService;
import sqlancer.common.log.LoggableFactory;

// import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.gen.FelderaInsertGenerator;
import sqlancer.feldera.query.FelderaOtherQuery;
import sqlancer.feldera.query.FelderaQueryProvider;

// import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("unused")
@AutoService(DatabaseProvider.class)
public class FelderaProvider extends ProviderAdapter<FelderaGlobalState, FelderaOptions, FelderaConnection> {

    protected String username;
    protected String password;
    protected String host;
    protected int port;
    protected String databaseName;

    public FelderaProvider() {
        super(FelderaGlobalState.class, FelderaOptions.class);
    }

    protected FelderaProvider(Class<FelderaGlobalState> globalClass, Class<FelderaOptions> optionClass) {
        super(globalClass, optionClass);
    }

    protected static int mapActions(FelderaGlobalState globalState, sqlancer.feldera.FelderaProvider.Action a) {
        Randomly r = globalState.getRandomly();
        int nrPerformed;
        if (Objects.requireNonNull(a) == sqlancer.feldera.FelderaProvider.Action.INSERT) {
            nrPerformed = r.getInteger(0, globalState.getOptions().getMaxNumberInserts());
        } else {
            throw new AssertionError(a);
        }
        return nrPerformed;

    }

    @Override
    protected void checkViewsAreValid(FelderaGlobalState globalState) {
    }

    @Override
    public void generateDatabase(FelderaGlobalState globalState) throws Exception {
        createTables(globalState, Randomly.fromOptions(4, 5, 6));
        prepareTables(globalState);

    }

    @Override
    public FelderaConnection createDatabase(@SuppressWarnings("unused") FelderaGlobalState globalState) throws Exception {

//        username = globalState.getOptions().getUserName();
//        password = globalState.getOptions().getPassword();
//        host = globalState.getOptions().getHost();
//        port = globalState.getOptions().getPort();
//        databaseName = globalState.getDatabaseName();
//        FelderaClient client = new FelderaClient(host, port, username, password, databaseName);
//        FelderaConnection connection = new FelderaConnection(client);
//        client.execute("DROP DATABASE IF EXISTS " + databaseName);
//        globalState.getState().logStatement("DROP DATABASE IF EXISTS " + databaseName);
//        client.execute("CREATE DATABASE " + databaseName);
//        globalState.getState().logStatement("CREATE DATABASE " + databaseName);

//        return connection;
        return null;
    }

    protected void createTables(FelderaGlobalState globalState, int numTables) throws Exception {
        while (globalState.getSchema().getDatabaseTables().size() < numTables) {
            String tableName = String.format("m%d", globalState.getSchema().getDatabaseTables().size());
//            FelderaOtherQuery createTable = FelderaTableGenerator.generate(tableName);
//            globalState.executeStatement(createTable);
        }
    }

    protected void prepareTables(FelderaGlobalState globalState) throws Exception {
        StatementExecutor<FelderaGlobalState, FelderaProvider.Action> se = new StatementExecutor<>(globalState, FelderaProvider.Action.values(),
                sqlancer.feldera.FelderaProvider::mapActions, (q) -> {
            if (globalState.getSchema().getDatabaseTables().isEmpty()) {
                throw new IgnoreMeException();
            }
        });
        se.executeStatements();
    }

    @Override
    public String getDBMSName() {
        return "feldera";
    }

    @Override
    public LoggableFactory getLoggableFactory() {
//        return new FelderaLoggableFactory();
        return null;
    }

    public enum Action implements AbstractAction<FelderaGlobalState> {
        INSERT(FelderaInsertGenerator::insert);

        private final FelderaQueryProvider<FelderaGlobalState> sqlQueryProvider;

        Action(FelderaQueryProvider<FelderaGlobalState> sqlQueryProvider) {
            this.sqlQueryProvider = sqlQueryProvider;
        }

        @Override
        public FelderaOtherQuery getQuery(FelderaGlobalState state) throws Exception {
//            return new FelderaOtherQuery(sqlQueryProvider.getQuery(state).getQueryString(),
//                    FelderaExpectedError.expectedErrors());
            return null;
        }
    }

}
