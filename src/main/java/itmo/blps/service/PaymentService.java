package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.dto.response.PaymentResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CardRepository cardRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CardService cardService;
    private final OrderService orderService;

    public PaymentResponse payOrder(String username, CardRequest cardRequest) {
        Order order = orderService.getOrder(null, username);
        System.out.println(order);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Order does not belong to the user");
        }
        if (!order.getIsConfirmed()) {
            throw new RuntimeException("Order is not confirmed");
        }
        if (order.getIsPaid()) {
            throw new RuntimeException("Order is already paid");
        }

        Card card;
        Optional<Card> cardOptional = cardRepository.findByUserId(user.getId());

        if (cardOptional.isEmpty()) {
            if (cardRequest == null) {
                throw new CardNotProvidedException("Card details are required");
            }
            CardResponse cardResponse = cardService.createCard(cardRequest, username);
            card = modelMapper.map(cardResponse, Card.class);
            card = cardRepository.save(card);
        } else {
            card = cardOptional.get();
        }

        if (card.getMoney() < order.getCost()) {
            throw new RuntimeException("Insufficient funds on the card");
        }
        card.setMoney(card.getMoney() - order.getCost());
        Card updatedCard = cardRepository.save(card);
        order.setIsPaid(true);
        orderRepository.save(order);
        return PaymentResponse.builder()
                .message("Payment successful")
                .card(updatedCard)
                .build();
    }
}
