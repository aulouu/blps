package itmo.blps.external;

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
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BankServiceClient {
    private final RestTemplate restTemplate;

    @Value("${bank.service.url}")
    private String bankServiceUrl;

    public ResponseEntity<String> withdraw(String cardNumber, Double amount) {
        String url = String.format("%s/withdraw", bankServiceUrl);

        BalanceRequest request = new BalanceRequest(cardNumber, amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BalanceRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new FailTransactionException(String.format("Bank operation failed: status %s", response.getStatusCode()));
        }
        return response;
    }

    public ResponseEntity<String> createCard(String cardNumber) {
        String url = String.format("%s/create", bankServiceUrl);

        ExternalCardRequest request = new ExternalCardRequest(cardNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ExternalCardRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new FailTransactionException(String.format("Bank operation failed: status %s", response.getStatusCode()));
        }
        return response;
    }
}
