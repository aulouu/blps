package itmo.blps.exceptions;

public class OrderNotBelongException extends RuntimeException {
    public OrderNotBelongException(String message) {
        super(message);
    }
}
