package sqlancer.feldera.client;

public class FelderaException extends Exception {
    private static final long serialVersionUID = 1L;
    public FelderaException(int statusCode) {
        super("got status code: " + statusCode);
    }
    @SuppressWarnings("unused")
    public FelderaException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public FelderaException(String message, int statusCode) {
        super("got status code: " + statusCode + "; error message: " + message);
    }
}
