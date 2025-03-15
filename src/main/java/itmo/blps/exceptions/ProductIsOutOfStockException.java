package itmo.blps.exceptions;

public class ProductIsOutOfStockException extends RuntimeException {
    public ProductIsOutOfStockException(String message) {
        super(message);
    }
}
