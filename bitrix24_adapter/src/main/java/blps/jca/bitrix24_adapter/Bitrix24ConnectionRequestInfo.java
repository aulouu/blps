package blps.jca.bitrix24_adapter;

import jakarta.resource.spi.ConnectionRequestInfo;

import java.util.Objects;

public class Bitrix24ConnectionRequestInfo implements ConnectionRequestInfo {
    private final String webhookUrl;

    public Bitrix24ConnectionRequestInfo(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bitrix24ConnectionRequestInfo that = (Bitrix24ConnectionRequestInfo) o;
        return Objects.equals(webhookUrl, that.webhookUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webhookUrl);
    }
}
