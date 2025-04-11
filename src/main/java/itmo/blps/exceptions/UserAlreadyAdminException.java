package itmo.blps.exceptions;

public class UserAlreadyAdminException extends RuntimeException {
    public UserAlreadyAdminException(String message) {
        super(message);
    }
}
