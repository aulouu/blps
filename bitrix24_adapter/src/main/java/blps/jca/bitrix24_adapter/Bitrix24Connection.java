package blps.jca.bitrix24_adapter;

import jakarta.json.JsonObject;
import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEvent;

import java.util.Map;

public class Bitrix24Connection implements AutoCloseable {
    private final Bitrix24ManagedConnection managedConnection;
    private final Bitrix24ApiClient apiClient;
    private boolean closed = false;

    public Bitrix24Connection(Bitrix24ManagedConnection managedConnection,
                              Bitrix24ApiClient apiClient) {
        this.managedConnection = managedConnection;
        this.apiClient = apiClient;
    }

    public JsonObject callMethod(String methodName, Map<String, Object> params)
            throws ResourceException {
        checkIfClosed();
        try {
            return apiClient.callMethod(methodName, params);
        } catch (Bitrix24ApiException e) {
            throw new ResourceException("Failed to call Bitrix24 API method", e);
        } catch (ProductNotFoundException e) {
            throw e;
        }
    }

    public void close() throws ResourceException {
        if (!closed) {
            closed = true;
            managedConnection.fireConnectionEvent(ConnectionEvent.CONNECTION_CLOSED, this, null);
        }
    }

    void invalidate() {
        closed = true;
    }

    private void checkIfClosed() throws ResourceException {
        if (closed) {
            throw new ResourceException("Connection is closed");
        }
    }
}
