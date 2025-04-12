package itmo.blps.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.blps.dto.request.BalanceRequest;
import itmo.blps.dto.request.ExternalCardRequest;
import itmo.blps.exceptions.FailTransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BankServiceClient {
    private final RestTemplate restTemplate;

    @Value("${bank.service.url}")
    private String bankServiceUrl;

    public ResponseEntity<?> withdraw(String cardNumber, Double amount) {
        String url = String.format("%s/withdraw", bankServiceUrl);

        BalanceRequest request = new BalanceRequest(cardNumber, amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BalanceRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = extractErrorMessage(response.getBody());
                throw new FailTransactionException(
                        String.format("Bank service returned: %s",
//                                response.getStatusCodeValue(),
                                errorMsg)
                );
            }
            return response;
        } catch (HttpStatusCodeException e) {
            String errorMsg = extractErrorMessage(e.getResponseBodyAsString());
            throw new FailTransactionException(
                    String.format("Bank service returned: %s",
//                            e.getRawStatusCode(),
                            errorMsg)
            );
        }
    }

    public ResponseEntity<?> createCard(String cardNumber) {
        String url = String.format("%s/create", bankServiceUrl);

        ExternalCardRequest request = new ExternalCardRequest(cardNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ExternalCardRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<?> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                String errorMsg = extractErrorMessage(response.getBody());
                throw new FailTransactionException(
                        String.format("Bank service returned %d: %s",
//                                response.getStatusCodeValue(),
                                errorMsg)
                );
            }
            return response;
        } catch (HttpStatusCodeException e) {
            String errorMsg = extractErrorMessage(e.getResponseBodyAsString());
            throw new FailTransactionException(
                    String.format("Bank service returned %d: %s",
//                            e.getRawStatusCode(),
                            errorMsg)
            );
        }
    }

    private String extractErrorMessage(Object responseBody) {
        if (responseBody == null) {
            return "Unknown error occurred";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody.toString());
            if (rootNode.has("message")) {
                return rootNode.get("message").asText();
            }
            return responseBody.toString();
        } catch (Exception e) {
            return responseBody.toString();
        }
    }
}
