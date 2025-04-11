package itmo.blps.external;

import itmo.blps.dto.request.BalanceRequest;
import itmo.blps.exceptions.FailTransactionException;
import jakarta.transaction.UserTransaction;
import jdk.dynalink.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BankServiceClient {
    private final RestTemplate restTemplate;
//    private final JtaTransactionManager transactionManager;

    @Value("${bank.service.url}")
    private String bankServiceUrl;

    public ResponseEntity<String> withdraw(String cardNumber, Double amount) {
//        UserTransaction userTransaction = transactionManager.getUserTransaction();
//        try {
//            // Начинаем вложенную транзакцию
//            if (userTransaction != null) userTransaction.begin();
//
//            try {
                String url = String.format("%s/withdraw", bankServiceUrl);

        BalanceRequest request = new BalanceRequest(cardNumber, amount);

        // 3. Настройка заголовков
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 4. Создание HTTP сущности
        HttpEntity<BalanceRequest> entity = new HttpEntity<>(request, headers);

        // 5. Отправка запроса
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        // 6. Проверка ответа
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new FailTransactionException(String.format("Bank operation failed: status %s", response.getStatusCode()));
        }

//                ResponseEntity<Operation> response = restTemplate.postForEntity(
//                        url, null, Operation.class);
//
//                if (!response.getStatusCode().is2xxSuccessful()) {
//                    throw new FailTransactionException(String.format("Withdrawal failed: %s", response.getBody()));
//                }

        return response;
//            } catch (Exception e) {
//                if (userTransaction != null) userTransaction.rollback();
//                throw new FailTransactionException(String.format("Bank operation failed: %s", e));
//            }
//        } catch (Exception e) {
//            throw new FailTransactionException(String.format("Transaction error: %s", e));
//        }
    }
}