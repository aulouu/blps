package itmo.blps.exceptions;

public class CardNotProvidedException extends IllegalArgumentException {
    public CardNotProvidedException(String message) {
        super(message);
    }
}
