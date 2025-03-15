package itmo.blps.exceptions;

public class AddressNotProvidedException extends IllegalArgumentException {
    public AddressNotProvidedException(String message) {
        super(message);
    }
}