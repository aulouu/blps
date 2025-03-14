package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

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

    public OrderResponse getCurrentOrder(String sessionId, String username) {
        Order order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            order = orderRepository.findByUserIdAndIsConfirmedFalse(user.getId())
                    .orElseGet(() -> Order.builder().user(user).cost(0.0).build());
        } else {
            order = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId)
                    .orElseGet(() -> Order.builder().sessionId(sessionId).cost(0.0).build());
        }
        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse addProductToOrder(ProductRequest productRequest, String sessionId, String username) {
        Stock productOnStock = stockRepository.findById(productRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product %s not found", productRequest.getProductId())
                ));
        if (productRequest.getCount() > productOnStock.getAmount()) {
            throw new ProductIsOutOfStockException(String.format("Amount of %s exceeds stock", productRequest.getProductId()));
        }

        Order order = getOrder(sessionId, username);

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

        return modelMapper.map(order, OrderResponse.class);
    }

    // TODO сброс количества после оплаты
    public OrderResponse confirmOrder(String sessionId, String username, ConfirmOrderRequest confirmOrderRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));

        Order order = getOrder(sessionId, username);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Order does not belong to the user");
        }

        if (order.getAddress() == null) {
            throw new AddressNotProvidedException("Address not specified");
        }

        if (confirmOrderRequest.getDeliveryTime() == null) {
            throw new IllegalArgumentException("Delivery time is required");
        }

        if (confirmOrderRequest.getUtensilsCount() == null || confirmOrderRequest.getUtensilsCount() <= 0) {
            throw new IllegalArgumentException("Utensils count must be a positive number");
        }

        LocalTime currentTime = LocalTime.now();
        LocalTime deliveryTime;
        try {
            deliveryTime = LocalTime.parse(confirmOrderRequest.getDeliveryTime());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid delivery time format. Expected HH:mm");
        }
        long differenceInMinutes = ChronoUnit.MINUTES.between(currentTime, deliveryTime);
        if (differenceInMinutes <= 60) {
            throw new IllegalArgumentException("Delivery time must be at least 1 hour from now");
        }

        order.setDeliveryTime(confirmOrderRequest.getDeliveryTime());
        order.setUtensilsCount(confirmOrderRequest.getUtensilsCount());

        order.setIsConfirmed(true);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    public void mergeOrder(String username, String sessionId) {
        Optional<Order> sessionOrderOptional = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId);
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
        Address address = addressRepository.findByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(
                addressRequest.getCity(), addressRequest.getStreet(), addressRequest.getBuilding(),
                addressRequest.getEntrance(), addressRequest.getFloor(), addressRequest.getFlat())
                .orElseGet(() -> {
                    Address newAddress = modelMapper.map(addressService.createAddress(addressRequest), Address.class);
                    return addressRepository.save(newAddress);
                });
        Order order = getOrder(sessionId, username);
        order.setAddress(address);
        order = orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    //TODO: save address after payment to user's addresses list
    public Order getOrder(String sessionId, String username) {
        Order order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Username %s not found", username)
                    ));
            order = orderRepository.findByUserIdAndIsConfirmedFalse(user.getId())
                    .orElseGet(() -> Order.builder().user(user).cost(0.0).isConfirmed(false).isPaid(false).build());
        } else {
            order = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId)
                    .orElseGet(() -> Order.builder().sessionId(sessionId).cost(0.0).isConfirmed(false).isPaid(false).build());
        }
        return order;
    }
}
