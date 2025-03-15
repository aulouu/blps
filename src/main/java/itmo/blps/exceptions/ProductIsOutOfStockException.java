package itmo.blps.exceptions;

public class ProductIsOutOfStockException extends IllegalArgumentException {
    public ProductIsOutOfStockException(String message) {
        super(message);
    }
}
