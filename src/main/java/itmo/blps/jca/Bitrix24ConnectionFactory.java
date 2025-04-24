package itmo.blps.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;

public class Bitrix24ConnectionFactory {
    private final Bitrix24ManagedConnectionFactory mcf;
    private final ConnectionManager cm;

    public Bitrix24ConnectionFactory(Bitrix24ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    public Bitrix24Connection getConnection() throws ResourceException {
        if (cm != null) {
            return (Bitrix24Connection) cm.allocateConnection(mcf, null);
        } else {
            Bitrix24ManagedConnection managedConnection =
                    (Bitrix24ManagedConnection) mcf.createManagedConnection(null, null);
            return (Bitrix24Connection) managedConnection.getConnection(null, null);
        }
    }

    public Bitrix24Connection getConnection(String webhookUrl, String authToken)
            throws ResourceException {
        Bitrix24ConnectionRequestInfo cri = new Bitrix24ConnectionRequestInfo(webhookUrl, authToken);
        if (cm != null) {
            return (Bitrix24Connection) cm.allocateConnection(mcf, cri);
        } else {
            Bitrix24ManagedConnection managedConnection =
                    (Bitrix24ManagedConnection) mcf.createManagedConnection(null, cri);
            return (Bitrix24Connection) managedConnection.getConnection(null, cri);
        }
    }
}
