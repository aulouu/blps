package itmo.blps.exceptions;

public class CardNotProvidedException extends RuntimeException {
    public CardNotProvidedException(String message) {
        super(message);
    }
}
