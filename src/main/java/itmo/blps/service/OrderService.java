package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderConfirmationResponse;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.Address;
import itmo.blps.model.Order;
import itmo.blps.model.Product;
import itmo.blps.model.User;
import itmo.blps.repository.AddressRepository;
import itmo.blps.repository.OrderRepository;
import itmo.blps.repository.ProductRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final StockService stockService;

    public static void validateDeliveryTime(String deliveryTimeInput) {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Текущие дата и время
        LocalDateTime deliveryDateTime;

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
        StockResponse productOnStock;
        try {
            productOnStock = stockService.getProductFromBitrix24ById(productRequest.getProductId());
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(
                    String.format("Product %s not found in Bitrix24", productRequest.getProductId())
            );
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

        Product productInOrder = productRepository.findByProductNameAndOrder(productOnStock.getName(), order)
                .orElseGet(Product.builder().productName(productOnStock.getName()).productPrice(productOnStock.getPrice()).count(0.0).order(order)::build);

        productInOrder.setCount(productInOrder.getCount() + productRequest.getCount());
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

        orderRepository.save(order);

        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderConfirmationResponse confirmOrder(String sessionId, String username, ConfirmOrderRequest confirmOrderRequest) {
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
        order = orderRepository.save(order);

        OrderConfirmationResponse response = modelMapper.map(order, OrderConfirmationResponse.class);
        return response;
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
