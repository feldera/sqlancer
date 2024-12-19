package sqlancer.feldera;

import sqlancer.*;

import com.google.auto.service.AutoService;
import sqlancer.common.log.LoggableFactory;

import sqlancer.common.oracle.TestOracle;
import sqlancer.feldera.gen.FelderaInsertGenerator;
import sqlancer.feldera.gen.FelderaTableGenerator;
import sqlancer.feldera.gen.FelderaViewGenerator;
import sqlancer.feldera.query.FelderaOtherQuery;
import sqlancer.feldera.query.FelderaQueryProvider;

import java.util.List;
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
        createViews(globalState, Randomly.fromOptions(4, 5, 6));
        prepareTables(globalState);
    }

    @Override
    public FelderaConnection createDatabase(FelderaGlobalState globalState) throws Exception {
        url = globalState.getDbmsSpecificOptions().connection_url;
        pipelineName = globalState.getDatabaseName();

        try (FelderaConnection connection = new FelderaConnection(url, pipelineName)) {
            try {
                connection.get();
                connection.shutdown();
            } catch (Exception ignored) {
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

    protected void createViews(FelderaGlobalState globalState, int numViews) throws Exception {
        for (int i = 0; i < numViews; i++) {
            String viewName = String.format("v%d", i);
            List<FelderaOtherQuery> views = FelderaViewGenerator.generate(globalState, viewName);
            for (FelderaOtherQuery view : views) {
                globalState.executeStatement(view);
                globalState.addView(viewName);
            }
        }
    }

    protected void prepareTables(FelderaGlobalState globalState) throws Exception {
        StatementExecutor<FelderaGlobalState, FelderaProvider.Action> se = new StatementExecutor<>(globalState,
                FelderaProvider.Action.values(), sqlancer.feldera.FelderaProvider::mapActions, (q) -> {
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
    public Reproducer<FelderaGlobalState> generateAndTestDatabase(FelderaGlobalState globalState) throws Exception {
        try {
            generateDatabase(globalState);
            globalState.getManager().incrementCreateDatabase();

            TestOracle<FelderaGlobalState> oracle = getTestOracle(globalState);
            try (StateToReproduce.OracleRunReproductionState localState = globalState.getState().createLocalState()) {
                assert localState != null;
                try {
                    oracle.check();
                    globalState.getManager().incrementSelectQueryCount();
                } catch (IgnoreMeException ignored) {
                } catch (AssertionError e) {
                    Reproducer<FelderaGlobalState> reproducer = oracle.getLastReproducer();
                    if (reproducer != null) {
                        return reproducer;
                    }
                    throw e;
                }
                localState.executedWithoutError();
            }
        } finally {
            globalState.getConnection().close();
        }
        return null;
    }

    @Override
    public LoggableFactory getLoggableFactory() {
        return new FelderaLoggableFactory();
    }

    public enum Action implements AbstractAction<FelderaGlobalState> {
        INSERT(FelderaInsertGenerator::getQuery);

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
