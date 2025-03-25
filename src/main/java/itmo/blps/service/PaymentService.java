package itmo.blps.service;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.dto.response.PaymentResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.Card;
import itmo.blps.model.Order;
import itmo.blps.model.User;
import itmo.blps.repository.CardRepository;
import itmo.blps.repository.OrderRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
            throw new OrderNotBelongException("Order does not belong to the user");
        }
        if (!order.getIsConfirmed()) {
            throw new NotValidOrderStatusException("Order is not confirmed");
        }
        if (order.getIsPaid()) {
            throw new NotValidOrderStatusException("Order is already paid");
        }

        validateCardRequest(cardRequest);

        String hashedCvv = new BCryptPasswordEncoder().encode(cardRequest.getCvv());
        YearMonth expirationDate = YearMonth.parse(cardRequest.getExpiration(),
                DateTimeFormatter.ofPattern("MM/yy"));
        if (expirationDate.isBefore(YearMonth.now())) {
            throw new NotValidInputException("Card has expired");
        }
        Card card;
        Optional<Card> cardOptional = cardRepository.findByUser(user);

        if (cardOptional.isEmpty()) {
            CardResponse cardResponse = cardService.createCard(cardRequest, username);
            card = modelMapper.map(cardResponse, Card.class);
            card.setUser(user);
            card.setCvv(hashedCvv);
            card.setMoney(0.0);
            card = cardRepository.save(card);
        } else {
            card = cardOptional.get();
            if (!cardRequest.getNumber().equals(card.getNumber())) {
                throw new NotValidInputException("Card number date does not match");
            }
            if (!cardRequest.getExpiration().equals(card.getExpiration())) {
                throw new NotValidInputException("Card expiration date does not match");
            }
            if (!new BCryptPasswordEncoder().matches(cardRequest.getCvv(), card.getCvv())) {
                throw new NotValidInputException("Invalid CVV");
            }
        }

        if (card.getMoney() < order.getCost()) {
            throw new NotEnoughMoneyException("Insufficient funds on the card");
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
