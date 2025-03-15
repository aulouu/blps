package itmo.blps.exceptions;

public class NotValidInputException extends RuntimeException {
    public NotValidInputException(String message) {
        super(message);
    }
}
