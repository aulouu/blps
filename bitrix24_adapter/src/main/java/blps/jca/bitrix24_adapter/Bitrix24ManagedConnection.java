package blps.jca.bitrix24_adapter;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Bitrix24ManagedConnection implements ManagedConnection {
    private final Bitrix24ManagedConnectionFactory factory;
    private final String webhookUrl;
    private final Bitrix24ApiClient apiClient;
    private final List<ConnectionEventListener> listeners;
    private final List<Bitrix24Connection> connections;
    private PrintWriter logWriter;

    public Bitrix24ManagedConnection(Bitrix24ManagedConnectionFactory factory,
                                     String webhookUrl) {
        this.factory = factory;
        this.webhookUrl = webhookUrl;
        this.apiClient = new Bitrix24ApiClient(webhookUrl);
        this.listeners = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cri) throws ResourceException {
        Bitrix24Connection connection = new Bitrix24Connection(this, apiClient);
        connections.add(connection);
        return connection;
    }

    @Override
    public void destroy() throws ResourceException {
        for (Bitrix24Connection connection : connections) {
            connection.invalidate();
        }
        connections.clear();
    }

    @Override
    public void cleanup() throws ResourceException {
        for (Bitrix24Connection connection : connections) {
            connection.invalidate();
        }
        connections.clear();
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (connection instanceof Bitrix24Connection bitrixConn) {
            connections.add(bitrixConn);
        } else {
            throw new ResourceException("Invalid connection type");
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new ManagedConnectionMetaData() {
            @Override
            public String getEISProductName() throws ResourceException {
                return "Bitrix24 API";
            }

            @Override
            public String getEISProductVersion() throws ResourceException {
                return "1.0";
            }

            @Override
            public int getMaxConnections() throws ResourceException {
                return factory.getMaxConnections();
            }

            @Override
            public String getUserName() throws ResourceException {
                return "Bitrix24User";
            }
        };
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    void fireConnectionEvent(int eventType, Object connection, Exception exception) {
        ConnectionEvent event = new ConnectionEvent(this, eventType, exception);
        event.setConnectionHandle(connection);

        for (ConnectionEventListener listener : listeners) {
            switch (eventType) {
                case ConnectionEvent.CONNECTION_CLOSED -> listener.connectionClosed(event);
                case ConnectionEvent.CONNECTION_ERROR_OCCURRED -> listener.connectionErrorOccurred(event);
                default -> {
                }
            }
        }
    }

    boolean credentialsMatch(String webhookUrl) {
        return this.webhookUrl.equals(webhookUrl);
    }
}
