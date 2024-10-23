package sqlancer.feldera.query;

@FunctionalInterface
public interface FelderaQueryProvider<S> {
    FelderaOtherQuery getQuery(S globalState) throws Exception;
}
