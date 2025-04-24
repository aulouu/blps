package itmo.blps.jca;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.*;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;

import javax.transaction.xa.XAResource;
import java.io.Serializable;

@Connector(
        displayName = "Bitrix24 Resource Adapter",
        vendorName = "ITMO",
        eisType = "Bitrix24 API",
        version = "1.0"
)
public class Bitrix24ResourceAdapter implements ResourceAdapter, Serializable {
    private static final long serialVersionUID = 1L;
    private transient BootstrapContext bootstrapContext;

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        this.bootstrapContext = ctx;
    }

    @Override
    public void stop() {
        // Cleanup resources if needed
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
            throws ResourceException {
        throw new UnsupportedOperationException("Inbound communication not supported");
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        // Not used for outbound communication
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return null;
    }
}
