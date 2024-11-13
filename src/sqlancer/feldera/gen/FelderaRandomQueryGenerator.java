package sqlancer.feldera.gen;

import sqlancer.Randomly;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.ast.FelderaExpression;
import sqlancer.feldera.ast.FelderaJoin;
import sqlancer.feldera.ast.FelderaSelect;
import sqlancer.feldera.ast.FelderaTableReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FelderaRandomQueryGenerator {
    private FelderaRandomQueryGenerator() {
    }

    public static FelderaSelect createRandomQuery(int nrColumns, FelderaGlobalState globalState) {
        FelderaSchema.FelderaTables targetTables = globalState.getSchema().getRandomTableNonEmptyTables();
        FelderaExpressionGenerator gen = new FelderaExpressionGenerator(globalState)
                .setColumns(targetTables.getColumns());
        FelderaSelect select = new FelderaSelect();
        select.setDistinct(Randomly.getBoolean());

        List<FelderaExpression> columns = new ArrayList<>();

        for (int i = 0; i < nrColumns; i++) {
            columns.add(gen.generateExpression(FelderaSchema.FelderaDataType.getRandomType()));
        }
        select.setFetchColumns(columns);
        List<FelderaSchema.FelderaTable> tables = targetTables.getTables();
        List<FelderaTableReference> tableList = tables.stream().map(FelderaTableReference::new)
                .collect(Collectors.toList());
        List<FelderaJoin> joins = FelderaJoin.getJoins(tableList, globalState);
        select.setJoinList(new ArrayList<>(joins));
        select.setFromList(new ArrayList<>(tableList));

        if (Randomly.getBoolean()) {
            select.setWhereClause(gen.generateExpression(FelderaSchema.FelderaDataType.BOOLEAN));
        }

        return select;
    }
}
