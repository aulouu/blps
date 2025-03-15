package itmo.blps.exceptions;

public class OrderNotFoundException extends IllegalArgumentException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
