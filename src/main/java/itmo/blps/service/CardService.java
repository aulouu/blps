package itmo.blps.service;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.Card;
import itmo.blps.model.Order;
import itmo.blps.model.User;
import itmo.blps.repository.CardRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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

    public List<CardResponse> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return modelMapper.map(cards, new TypeToken<List<CardResponse>>() {
        }.getType());
    }

    public CardResponse getCardById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(
                    String.format("Card with id %d not found", id)
            );
        }
        return modelMapper.map(cardRepository.findById(id), CardResponse.class);
    }

    public CardResponse createCard(CardRequest cardRequest, String username) {
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
        return modelMapper.map(savedCard, CardResponse.class);
    }

    public CardResponse topUpBalance(String cardNumber, String username, Double amount) {
        if (amount <= 0) {
            throw new NotValidInputException("Amount must be positive");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));
        Card card = cardRepository.findByNumberAndUser(cardNumber, user)
                .orElseThrow(() -> new CardNotFoundException(
                        String.format("Card with number %s not found or doesn't belong to user %s. Check card number", cardNumber, username)
                ));
        card.setMoney(card.getMoney() + amount);
        Card updatedCard = cardRepository.save(card);
        return modelMapper.map(updatedCard, CardResponse.class);
    }
}
