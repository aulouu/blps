package itmo.blps.exceptions;

public class AddressNotFoundException extends IllegalArgumentException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
