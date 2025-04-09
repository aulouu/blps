package itmo.blps.exceptions;

public class OrderAlreadyConfirmedException extends RuntimeException {
    public OrderAlreadyConfirmedException(String message) {
        super(message);
    }
}
