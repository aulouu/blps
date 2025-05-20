package itmo.blps.exceptions;

public class BankUnavailableException extends RuntimeException {
    public BankUnavailableException(String message) {
        super(message);
    }
}
