package blps.jca.bitrix24_adapter;

public class Bitrix24ApiException extends Exception {
    public Bitrix24ApiException(String message) {
        super(message);
    }

    public Bitrix24ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
