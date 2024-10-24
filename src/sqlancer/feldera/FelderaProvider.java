package sqlancer.feldera;

import sqlancer.*;

import com.google.auto.service.AutoService;
import sqlancer.common.log.LoggableFactory;

import sqlancer.feldera.client.FelderaClient;
import sqlancer.feldera.gen.FelderaInsertGenerator;
import sqlancer.feldera.gen.FelderaTableGenerator;
import sqlancer.feldera.query.FelderaOtherQuery;
import sqlancer.feldera.query.FelderaQueryProvider;

import java.util.HashMap;
import java.util.Objects;

@AutoService(DatabaseProvider.class)
public class FelderaProvider extends ProviderAdapter<FelderaGlobalState, FelderaOptions, FelderaConnection> {

    protected String url;
    protected String pipelineName;

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
    public FelderaConnection createDatabase(FelderaGlobalState globalState) throws Exception {

        url = globalState.getDbmsSpecificOptions().connection_url;
        pipelineName = globalState.getDatabaseName();
        FelderaClient client = new FelderaClient(url, pipelineName);

        try (FelderaConnection connection = new FelderaConnection(client)) {
            HashMap<String, Object> map = client.get();
            if (map != null) {
                globalState.getState().logStatement("pipeline " + pipelineName + " exists, shutting down");
                client.shutdown();
            }

            return connection;
        }
    }

    protected void createTables(FelderaGlobalState globalState, int numTables) throws Exception {
        for (int i = 0; i < numTables; i++) {
            String tableName = String.format("t%d", i);
            FelderaTableGenerator generator = new FelderaTableGenerator(tableName);
            FelderaOtherQuery createTable = generator.generate();
            FelderaSchema.FelderaTable table = generator.getTable();
            globalState.addTable(table);
            globalState.executeStatement(createTable);
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
        return new FelderaLoggableFactory();
    }

    public enum Action implements AbstractAction<FelderaGlobalState> {
        INSERT(FelderaInsertGenerator::insert);

        private final FelderaQueryProvider<FelderaGlobalState> sqlQueryProvider;

        Action(FelderaQueryProvider<FelderaGlobalState> sqlQueryProvider) {
            this.sqlQueryProvider = sqlQueryProvider;
        }

        @Override
        public FelderaOtherQuery getQuery(FelderaGlobalState state) throws Exception {
            return new FelderaOtherQuery(sqlQueryProvider.getQuery(state).getQueryString(),
                    FelderaExpectedError.expectedErrors());
        }
    }

}
