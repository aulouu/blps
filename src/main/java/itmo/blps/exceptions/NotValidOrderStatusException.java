package itmo.blps.exceptions;

public class NotValidOrderStatusException extends RuntimeException {
    public NotValidOrderStatusException(String message) {
        super(message);
    }
}
