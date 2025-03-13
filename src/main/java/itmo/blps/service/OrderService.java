package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
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
        Product product = productRepository.findByName(productRequest.getName())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product %s not found", productRequest.getName())
                ));
        if (productRequest.getAmount() > product.getStock()) {
            throw new ProductIsOutOfStockException(String.format("Amount of %s exceeds stock", productRequest.getName()));
        }

        Order order = getOrder(sessionId, username);

        if (order.getAddress() == null) {
            throw new AddressNotProvidedException("Address not provided");
        }

        order.setCost(order.getCost() + product.getPrice());
        product.setAmount(product.getAmount() - productRequest.getAmount());

        Optional<Product> exist = order.getProducts().stream()
                .filter(p -> Objects.equals(p.getId(), product.getId()))
                .findFirst();

        if (exist.isPresent()) {
            exist.get().setAmount(exist.get().getAmount() + productRequest.getAmount()); // TODO не прибалвяется
            exist.get().setStock(exist.get().getStock() - productRequest.getAmount());
            productRepository.save(exist.get());
        } else {
            product.setAmount(productRequest.getAmount());
            product.setStock(product.getStock() - productRequest.getAmount());
            order.getProducts().add(product);
            productRepository.save(product);
        }

        order = orderRepository.save(order);

        return modelMapper.map(order, OrderResponse.class);
    }

    // TODO сброс количества после конферма
    public OrderResponse confirmOrder(String sessionId, String username) {
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

        order.setIsConfirmed(true);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    // TODO поправить сессии
    public void mergeOrder(String username, String sessionId) {
        Order sessionOrder = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

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

    private Order getOrder(String sessionId, String username) {
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
