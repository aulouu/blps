package itmo.blps.exceptions;

public class AddressNotProvidedException extends RuntimeException {
    public AddressNotProvidedException(String message) {
        super(message);
    }
}
