package itmo.blps.exceptions;

public class UserNotAuthorizedException extends IllegalArgumentException {
    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
