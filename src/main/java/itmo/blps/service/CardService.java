package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.AddressResponse;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.exceptions.AddressNotFoundException;
import itmo.blps.exceptions.CardNotFoundException;
import itmo.blps.exceptions.UserNotFoundException;
import itmo.blps.model.Address;
import itmo.blps.model.Card;
import itmo.blps.model.User;
import itmo.blps.repository.CardRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

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
        Card savedCard = cardRepository.save(card);
        return modelMapper.map(savedCard, CardResponse.class);
    }
}
