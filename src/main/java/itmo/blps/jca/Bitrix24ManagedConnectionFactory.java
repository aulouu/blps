package itmo.blps.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import lombok.Getter;
import lombok.Setter;

import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Set;

@Getter
@Setter
@ConnectionDefinition(
        connectionFactory = Bitrix24ConnectionFactory.class,
        connectionFactoryImpl = Bitrix24ConnectionFactory.class,
        connection = Bitrix24Connection.class,
        connectionImpl = Bitrix24Connection.class
)
public class Bitrix24ManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {
    private static final long serialVersionUID = 1L;

    private String defaultWebhookUrl;
    private String defaultAuthToken;
    private int maxConnections = 10;
    private PrintWriter logWriter;
    private Bitrix24ResourceAdapter resourceAdapter;

    @Override
    public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
        return new Bitrix24ConnectionFactory(this, cm);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return new Bitrix24ConnectionFactory(this, null);
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cri)
            throws ResourceException {

        String webhookUrl = this.defaultWebhookUrl;
//        String authToken = this.defaultAuthToken;

        if (cri != null && cri instanceof Bitrix24ConnectionRequestInfo) {
            Bitrix24ConnectionRequestInfo bitrixCri = (Bitrix24ConnectionRequestInfo) cri;
            webhookUrl = bitrixCri.getWebhookUrl();
//            authToken = bitrixCri.getAuthToken();
        }

        return new Bitrix24ManagedConnection(this, webhookUrl);
    }

    @Override
    public ManagedConnection matchManagedConnections(Set connections, Subject subject,
                                                     ConnectionRequestInfo cri) throws ResourceException {

        for (Object connection : connections) {
            if (connection instanceof Bitrix24ManagedConnection managedConn) {
                if (cri instanceof Bitrix24ConnectionRequestInfo bitrixCri) {
                    if (managedConn.credentialsMatch(
                            bitrixCri.getWebhookUrl())) {
                        return managedConn;
                    }
                } else if (defaultWebhookUrl != null) {
                    if (managedConn.credentialsMatch(defaultWebhookUrl)) {
                        return managedConn;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return this.logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
        this.logWriter = out;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return this.resourceAdapter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
        if (ra != null && !(ra instanceof Bitrix24ResourceAdapter)) {
            throw new ResourceException("Invalid resource adapter provided");
        }
        this.resourceAdapter = (Bitrix24ResourceAdapter) ra;
    }
}
