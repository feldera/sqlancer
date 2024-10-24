package sqlancer.feldera.gen;

import sqlancer.common.gen.AbstractInsertGenerator;
import sqlancer.feldera.FelderaGlobalState;
import sqlancer.feldera.FelderaSchema;
import sqlancer.feldera.query.FelderaOtherQuery;

public class FelderaInsertGenerator extends AbstractInsertGenerator<FelderaSchema.FelderaColumn> {

    private FelderaInsertGenerator() {}

    @Override
    protected void insertValue(FelderaSchema.FelderaColumn column) {

    }

    public static FelderaOtherQuery insert(FelderaGlobalState globalState) {
        return null;
    }
}
