package itmo.blps.jca;

import itmo.blps.exceptions.ProductNotFoundException;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;

import java.io.StringReader;
import java.util.Map;

@Slf4j
public class Bitrix24ApiClient {
    private final String webhookUrl;
    private final Client client;

    public Bitrix24ApiClient(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.client = ClientBuilder.newClient(new ClientConfig());
    }

    public JsonObject callMethod(String methodName, Map<String, Object> params)
            throws Bitrix24ApiException {
        String baseUrl = webhookUrl + methodName + ".json";

        UriBuilder uriBuilder = UriBuilder.fromUri(baseUrl);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        String fullUrl = uriBuilder.build().toString();

        try {
            Response response = client.target(fullUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            String responseBody = response.readEntity(String.class);
            if (response.getStatus() == 400 && responseBody.contains("Product is not found")) {
                throw new ProductNotFoundException("Product not found in Bitrix24");
            }

            if (response.getStatus() != 200) {
                throw new Bitrix24ApiException(
                        "Bitrix24 API returned status " + response.getStatus() +
                                ". Response: " + responseBody);
            }

            return Json.createReader(new StringReader(responseBody)).readObject();
        } catch (ProcessingException e) {
            throw new Bitrix24ApiException("Failed to process Bitrix24 API request", e);
        } catch (Exception e) {
            throw new Bitrix24ApiException("Unexpected error calling Bitrix24 API", e);
        }
    }

    public void close() {
        client.close();
    }
}
