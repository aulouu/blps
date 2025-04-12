package itmo.blps.exceptions;

public class AdminRequestNotFoundException extends RuntimeException {
    public AdminRequestNotFoundException(String message) {
        super(message);
    }
}
