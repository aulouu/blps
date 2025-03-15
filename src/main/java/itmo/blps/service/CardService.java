package itmo.blps.service;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.CardNotFoundException;
import itmo.blps.exceptions.UserNotFoundException;
import itmo.blps.model.Card;
import itmo.blps.model.User;
import itmo.blps.repository.CardRepository;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.HashUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    public List<CardResponse> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return modelMapper.map(cards, new TypeToken<List<CardResponse>>(){}.getType());
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
        if (cardRepository.existsByNumberAndExpirationAndCvvAndMoney(
                cardRequest.getNumber(),
                cardRequest.getExpiration(),
                cardRequest.getCvv(),
                cardRequest.getMoney())) {
            throw new CardNotFoundException("Card already exists");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));
        Card card = modelMapper.map(cardRequest, Card.class);
        card.setUser(user);
        String hashedCvv = HashUtil.hashCvv(cardRequest.getCvv());
        card.setCvv(hashedCvv);
        Card savedCard = cardRepository.save(card);
        return modelMapper.map(savedCard, CardResponse.class);
    }

    public static void validateCardRequest(CardRequest cardRequest) throws IllegalArgumentException {
        if (cardRequest.getNumber() == null || cardRequest.getNumber().length() != 16 || !cardRequest.getNumber().matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Card number must have 16 numbers");
        }
        if (cardRequest.getExpiration() == null || cardRequest.getExpiration().isEmpty() || !cardRequest.getExpiration().matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
            throw new IllegalArgumentException("Expire date must be in the format MM/yy");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expirationDate = YearMonth.parse(cardRequest.getExpiration(), formatter);
            YearMonth currentDate = YearMonth.now();
            if (expirationDate.isBefore(currentDate)) {
                throw new IllegalArgumentException("Date expired");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid expire date format");
        }
        if (cardRequest.getCvv() == null || cardRequest.getCvv().length() != 3) {
            throw new IllegalArgumentException("CVV must have 3 numbers");
        }
        if (cardRequest.getMoney() == null || cardRequest.getMoney() <= 0) {
            throw new IllegalArgumentException("Card balance must be positive");
        }
    }
}
