package itmo.blps.exceptions;

public class FailTransactionException extends RuntimeException {
    public FailTransactionException(String message) {
        super(message);
    }
}
