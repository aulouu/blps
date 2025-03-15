package itmo.blps.service;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.dto.response.PaymentResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import itmo.blps.security.HashUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static itmo.blps.service.CardService.validateCardRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CardRepository cardRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CardService cardService;

    public PaymentResponse payOrder(String username, CardRequest cardRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));
        Order order = orderRepository.findByUserIdAndIsConfirmedTrueAndIsPaidFalse(user.getId())
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order not found for payment, user %s", username)
                ));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to the user");
        }
        if (!order.getIsConfirmed()) {
            throw new IllegalArgumentException("Order is not confirmed");
        }
        if (order.getIsPaid()) {
            throw new IllegalArgumentException("Order is already paid");
        }

        validateCardRequest(cardRequest);

        String hashedCvv = HashUtil.hashCvv(cardRequest.getCvv());

        Card card;
        Optional<Card> cardOptional = cardRepository.findByUserIdAndNumberAndExpirationAndCvvAndMoney(
                user.getId(), cardRequest.getNumber(), cardRequest.getExpiration(),
                cardRequest.getCvv(), cardRequest.getMoney());

        if (cardOptional.isEmpty()) {
            CardResponse cardResponse = cardService.createCard(cardRequest, username);
            card = modelMapper.map(cardResponse, Card.class);
            card.setUser(user);
            card.setCvv(hashedCvv);
            card = cardRepository.save(card);
        } else {
            card = cardOptional.get();
            if (!card.getCvv().equals(hashedCvv)) {
                throw new IllegalArgumentException("Invalid CVV");
            }
        }

        if (card.getMoney() < order.getCost()) {
            throw new IllegalArgumentException("Insufficient funds on the card");
        }
        card.setMoney(card.getMoney() - order.getCost());
        cardRepository.save(card);
        order.setIsPaid(true);
        orderRepository.save(order);
        if (user.getAddresses().stream().noneMatch(address ->
                address.getStreet().equals(order.getAddress().getStreet()) &&
                        address.getCity().equals(order.getAddress().getCity()) &&
                        address.getBuilding().equals(order.getAddress().getBuilding()) &&
                        address.getEntrance().equals(order.getAddress().getEntrance()) &&
                        address.getFloor().equals(order.getAddress().getFloor()) &&
                        address.getFlat().equals(order.getAddress().getFlat())
        )) {
            user.getAddresses().add(order.getAddress());
        }
        return PaymentResponse.builder()
                .message("Payment successful")
                .order(modelMapper.map(order, OrderResponse.class))
                .build();
    }
}
