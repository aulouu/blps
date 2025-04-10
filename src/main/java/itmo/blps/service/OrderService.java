package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static itmo.blps.service.AddressService.validateAddressRequest;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final JtaTransactionManager transactionManager;

    public static void validateDeliveryTime(String deliveryTimeInput) {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Текущие дата и время
        LocalDateTime deliveryDateTime;

        // Формат для ввода: "день.месяц часы:минуты" (например, "12.10 14:30")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");

        try {
            // Парсим введенное время доставки
            // Поскольку ввод содержит только день и месяц, добавляем текущий год
            String inputWithYear = deliveryTimeInput + " " + currentDateTime.getYear();
            DateTimeFormatter formatterWithYear = DateTimeFormatter.ofPattern("dd.MM HH:mm yyyy");
            deliveryDateTime = LocalDateTime.parse(inputWithYear, formatterWithYear);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid delivery time format. Expected format: 'dd.MM HH:mm'");
        }

        // Проверка, что дата доставки не раньше текущей даты
        if (deliveryDateTime.toLocalDate().isBefore(currentDateTime.toLocalDate())) {
            throw new NotValidInputException("Delivery date cannot be in the past");
        }

        // Если дата доставки сегодня
        if (deliveryDateTime.toLocalDate().isEqual(currentDateTime.toLocalDate())) {
            // Проверка, что время доставки не раньше текущего времени
            if (deliveryDateTime.toLocalTime().isBefore(currentDateTime.toLocalTime())) {
                throw new NotValidInputException("Delivery time cannot be in the past for today");
            }

            // Проверка, что время доставки не менее чем через час от текущего времени
            long differenceInMinutes = ChronoUnit.MINUTES.between(currentDateTime.toLocalTime(), deliveryDateTime.toLocalTime());
            if (differenceInMinutes <= 60) {
                throw new NotValidInputException("Delivery time must be at least 1 hour from now for today");
            }
        }
    }

    public OrderResponse getCurrentOrder(String sessionId, String username) {
        Order order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            order = orderRepository.findFirstByUserIdOrderByCreationTimeDesc(user.getId())
                    .orElseGet(() -> Order.builder().user(user).cost(0.0).creationTime(LocalDateTime.now()).build());
        } else {
            order = orderRepository.findFirstBySessionIdOrderByCreationTimeDesc(sessionId)
                    .orElseGet(() -> Order.builder().sessionId(sessionId).cost(0.0).creationTime(LocalDateTime.now()).build());
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse addProductToOrder(ProductRequest productRequest, String sessionId, String username) {
        UserTransaction userTransaction = null;
        try {
            userTransaction = transactionManager.getUserTransaction();
            if (userTransaction != null) userTransaction.begin();
            Stock productOnStock = stockRepository.findById(productRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            String.format("Product %s not found", productRequest.getProductId())
                    ));
            if (productRequest.getCount() > productOnStock.getAmount()) {
                throw new ProductIsOutOfStockException(String.format("Amount of %s exceeds stock", productRequest.getProductId()));
            }
            if (productRequest.getCount() <= 0) {
                throw new NotValidInputException("Count must be positive");
            }

            Optional<Order> tryOrder = getActiveOrder(sessionId, username);
            Order order;
            if (tryOrder.isEmpty()) {
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent()) {
                    Optional<Order> checkConfirmed = orderRepository.
                            findFirstByUserIdAndIsConfirmedTrueAndIsPaidFalseOrderByCreationTimeDesc(user.get().getId());
                    if (checkConfirmed.isPresent()) {
                        throw new OrderAlreadyConfirmedException("Order already confirmed, you can't add products");
                    } else {
                        order = createEmptyOrder(sessionId, username);
                    }
                } else {
                    order = createEmptyOrder(sessionId, username);
                }
            } else {
                order = tryOrder.get();
            }


            if (order.getAddress() == null) {
                throw new AddressNotProvidedException("Address not provided");
            }

            Product productInOrder = productRepository.findByProductOnStockAndOrder(productOnStock, order)
                    .orElseGet(Product.builder().productOnStock(productOnStock).count(0.0).order(order)::build);

            productOnStock.setAmount(productOnStock.getAmount() - productRequest.getCount());
            productInOrder.setCount(productInOrder.getCount() + productRequest.getCount());

            stockRepository.save(productOnStock);
            productRepository.save(productInOrder);

            Optional<Product> exist = order.getProducts().stream()
                    .filter(p -> Objects.equals(p.getId(), productInOrder.getId()))
                    .findFirst();

            if (exist.isEmpty()) {
                order.getProducts().add(productInOrder);
            }

            double totalCost = order.getProducts().stream()
                    .mapToDouble(p -> productOnStock.getPrice() * productInOrder.getCount())
                    .sum();
            order.setCost(totalCost);

            order = orderRepository.save(order);

            if (userTransaction != null) userTransaction.commit();
            return modelMapper.map(order, OrderResponse.class);
        } catch (Exception e) {
            try {
                if (userTransaction != null) {
                    userTransaction.rollback();
                }
            } catch (SystemException ex) {
                throw new FailTransactionException("Failed to rollback transaction");
            }
            throw new FailTransactionException("Transaction failed");
        }
    }

    public OrderResponse confirmOrder(String sessionId, String username, ConfirmOrderRequest confirmOrderRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));

        Optional<Order> tryOrder = getActiveOrder(sessionId, username);
        Order order;
        if (tryOrder.isEmpty()) {
            Optional<Order> checkConfirmed = orderRepository.
                    findFirstByUserIdAndIsConfirmedTrueAndIsPaidFalseOrderByCreationTimeDesc(user.getId());
            if (checkConfirmed.isPresent()) {
                throw new OrderAlreadyConfirmedException("Order already confirmed");
            } else {
                order = createEmptyOrder(sessionId, username);
            }
        } else {
            order = tryOrder.get();
        }

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotBelongException("Order does not belong to the user");
        }

        if (order.getAddress() == null) {
            throw new AddressNotProvidedException("Address not specified");
        }

        if (confirmOrderRequest.getDeliveryTime() == null || confirmOrderRequest.getDeliveryTime().isEmpty()) {
            throw new IllegalArgumentException("Delivery time is required");
        }

        if (confirmOrderRequest.getUtensilsCount() == null || confirmOrderRequest.getUtensilsCount() < 0) {
            throw new IllegalArgumentException("Utensils count must be a positive number");
        }

        validateDeliveryTime(confirmOrderRequest.getDeliveryTime());

        order.setDeliveryTime(confirmOrderRequest.getDeliveryTime());
        order.setUtensilsCount(confirmOrderRequest.getUtensilsCount());

        order.setIsConfirmed(true);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    public void mergeOrder(String username, String sessionId) {
        Optional<Order> sessionOrderOptional = orderRepository.findFirstBySessionIdAndIsConfirmedFalseOrderByCreationTimeDesc(sessionId);
        if (sessionOrderOptional.isEmpty()) {
            return;
        }
        Order sessionOrder = sessionOrderOptional.get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));

        sessionOrder.setUser(user);
        sessionOrder.setSessionId(null);
        Order savedOrder = orderRepository.save(sessionOrder);

        modelMapper.map(savedOrder, OrderResponse.class);
    }

    public OrderResponse setAddress(AddressRequest addressRequest, String sessionId, String username) {
        validateAddressRequest(addressRequest);
        Address address = addressRepository.findByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(
                        addressRequest.getCity(), addressRequest.getStreet(), addressRequest.getBuilding(),
                        addressRequest.getEntrance(), addressRequest.getFloor(), addressRequest.getFlat())
                .orElseGet(() -> {
                    Address newAddress = modelMapper.map(addressService.createAddress(addressRequest), Address.class);
                    return addressRepository.save(newAddress);
                });
        Optional<Order> tryOrder = getActiveOrder(sessionId, username);
        Order order;
        order = tryOrder.orElseGet(() -> createEmptyOrder(sessionId, username));
        order.setAddress(address);
        order = orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    public Optional<Order> getActiveOrder(String sessionId, String username) {
        Optional<Order> order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            order = orderRepository.findFirstByUserIdAndIsConfirmedFalseOrderByCreationTimeDesc(user.getId());
        } else {
            order = orderRepository.findFirstBySessionIdAndIsConfirmedFalseOrderByCreationTimeDesc(sessionId);
        }
        return order;
    }

    public Order createEmptyOrder(String sessionId, String username) {
        Order order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            order = Order.builder().user(user).cost(0.0).isConfirmed(false).isPaid(false).creationTime(LocalDateTime.now()).build();
        } else {
            order = Order.builder().sessionId(sessionId).cost(0.0).isConfirmed(false).isPaid(false).creationTime(LocalDateTime.now()).build();
        }
        return order;
    }

    public List<OrderResponse> getAllPaidOrders() {
        List<Order> paidOrders = orderRepository.findAllByIsPaidTrue();
        return paidOrders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllConfirmedOrders() {
        List<Order> paidOrders = orderRepository.findAllByIsConfirmedTrue();
        return paidOrders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }
}
