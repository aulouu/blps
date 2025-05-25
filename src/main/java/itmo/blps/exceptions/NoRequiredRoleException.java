package itmo.blps.exceptions;

public class NoRequiredRoleException extends RuntimeException {
    public NoRequiredRoleException(String message) {
        super(message);
    }
}
