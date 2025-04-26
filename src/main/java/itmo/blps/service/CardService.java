package itmo.blps.service;

import itmo.blps.dto.request.BalanceRequest;
import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.*;
import itmo.blps.external.BankServiceClient;
import itmo.blps.model.Card;
import itmo.blps.model.User;
import itmo.blps.repository.CardRepository;
import itmo.blps.repository.UserRepository;
import jakarta.jms.Queue;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BankServiceClient bankServiceClient;
    private final TransactionManager transactionManager;
    private final JmsTemplate jmsTemplate;
    private final Queue cardCreateQueue;
    private final Queue cardSubmitQueue;

    public static void validateCardRequest(CardRequest cardRequest) throws IllegalArgumentException {
        if (cardRequest.getNumber() == null || cardRequest.getNumber().length() != 16 || !cardRequest.getNumber().matches("^[0-9]+$")) {
            throw new NotValidInputException("Card number must have 16 numbers");
        }
        if (cardRequest.getExpiration() == null || cardRequest.getExpiration().isEmpty() || !cardRequest.getExpiration().matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
            throw new NotValidInputException("Expire date must be in the format MM/yy");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expirationDate = YearMonth.parse(cardRequest.getExpiration(), formatter);
            YearMonth currentDate = YearMonth.now();
            if (expirationDate.isBefore(currentDate)) {
                throw new NotValidInputException("Date expired");
            }
        } catch (DateTimeParseException e) {
            throw new NotValidInputException("Invalid expire date format");
        }
        if (cardRequest.getCvv() == null || cardRequest.getCvv().length() != 3) {
            throw new NotValidInputException("CVV must have 3 numbers");
        }
    }

    public CardResponse createCard(CardRequest cardRequest, String username) {
        try {
            transactionManager.begin();
            validateCardRequest(cardRequest);
            if (cardRepository.existsByNumber(cardRequest.getNumber())) {
                throw new CardAlreadyExistsException("Card already exists. Check card number");
            }
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            Card card = modelMapper.map(cardRequest, Card.class);
            card.setUser(user);
            String hashedCvv = new BCryptPasswordEncoder().encode(cardRequest.getCvv());
            card.setCvv(hashedCvv);
            card.setMoney(0.0);

            Card savedCard = cardRepository.save(card);
//            bankServiceClient.createCard(cardRequest.getNumber());

            String correlationId = UUID.randomUUID().toString();
            jmsTemplate.convertAndSend(cardCreateQueue, card.getNumber(), message -> {
                message.setJMSCorrelationID(correlationId);
                return message;
            });

            Boolean confirmation = (Boolean) jmsTemplate.receiveSelectedAndConvert(
                    cardSubmitQueue,
                    "JMSCorrelationID = '" + correlationId + "'"
            );

            if (confirmation == null) {
                throw new FailTransactionException("Bank response timeout");
            }
            if (!confirmation) {
                throw new FailTransactionException("Bank rejected card creation");
            }

            transactionManager.commit();
            return modelMapper.map(savedCard, CardResponse.class);
        } catch (Exception e) {
            try {
                transactionManager.rollback();
            } catch (SystemException ex) {
                throw new FailTransactionException(String.format("Failed to rollback transaction: %s", ex.getMessage()));
            }
            throw new FailTransactionException(String.format("Transaction failed: %s", e.getMessage()));
        }
    }

    public CardResponse topUpBalance(BalanceRequest balanceRequest, String username) {
        try {
            transactionManager.begin();
            if (balanceRequest.getMoney() <= 0) {
                throw new NotValidInputException("Money must be positive");
            }
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            Card card = cardRepository.findByNumberAndUser(balanceRequest.getNumber(), user)
                    .orElseThrow(() -> new CardNotFoundException(
                            String.format("Card with number %s not found or doesn't belong to user %s. Check card number", balanceRequest.getNumber(), username)
                    ));
            ResponseEntity<?> resp = bankServiceClient.withdraw(balanceRequest.getNumber(), balanceRequest.getMoney());
            if (resp.getStatusCode() != HttpStatus.OK) {
                throw new FailTransactionException("Bank operation failed");
            }
            card.setMoney(card.getMoney() + balanceRequest.getMoney());
            Card updatedCard = cardRepository.save(card);
            transactionManager.commit();
            return modelMapper.map(updatedCard, CardResponse.class);
        } catch (Exception e) {
            try {
                transactionManager.rollback();
            } catch (SystemException ex) {
                throw new FailTransactionException(String.format("Failed to rollback transaction: %s", ex.getMessage()));
            }
            throw new FailTransactionException(String.format("Transaction failed: %s", e.getMessage()));
        }
    }
}
