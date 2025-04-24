package itmo.blps.jca;

import jakarta.resource.spi.ConnectionRequestInfo;

import java.util.Objects;

public class Bitrix24ConnectionRequestInfo implements ConnectionRequestInfo {
    private final String webhookUrl;
    private final String authToken;

    public Bitrix24ConnectionRequestInfo(String webhookUrl, String authToken) {
        this.webhookUrl = webhookUrl;
        this.authToken = authToken;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bitrix24ConnectionRequestInfo that = (Bitrix24ConnectionRequestInfo) o;
        return Objects.equals(webhookUrl, that.webhookUrl) &&
                Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webhookUrl, authToken);
    }
}
