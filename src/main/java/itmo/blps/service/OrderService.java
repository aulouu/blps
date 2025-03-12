package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.*;
import itmo.blps.model.*;
import itmo.blps.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public OrderResponse addProductToOrder(Long productId, String sessionId, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Order order = getOrder(sessionId, username);

        if (order.getAddress() == null) {
            throw new AddressNotProvidedException("Address not provided");
        }

        order.getProducts().add(product);
        order.setCost(order.getCost() + product.getPrice());
        order = orderRepository.save(order);

        return modelMapper.map(order, OrderResponse.class);
    }

    public OrderResponse confirmOrder(Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Order does not belong to the user");
        }

        if (order.getAddress() == null) {
            throw new RuntimeException("Address not specified");
        }

        order.setIsConfirmed(true);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    public void mergeOrder(Long userId, String sessionId) {
        Order sessionOrder = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        sessionOrder.setUser(user);
        sessionOrder.setSessionId(null);
        Order savedOrder = orderRepository.save(sessionOrder);

        modelMapper.map(savedOrder, OrderResponse.class);
    }

    public OrderResponse setAddress(AddressRequest addressRequest, String sessionId, String username) {
        Address address = addressRepository.findByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(
                addressRequest.getCity(), addressRequest.getStreet(), addressRequest.getBuilding(),
                addressRequest.getEntrance(), addressRequest.getFloor(), addressRequest.getFlat())
                .orElse(modelMapper.map(addressService.createAddress(addressRequest), Address.class));
        Order order = getOrder(sessionId, username);
        order.setAddress(address);
        return modelMapper.map(order, OrderResponse.class);
    }

    //TODO: save address after payment to user's addresses list

    private Order getOrder(String sessionId, String username) {
        Order order;
        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            order = orderRepository.findByUserIdAndIsConfirmedFalse(user.getId())
                    .orElseGet(() -> Order.builder().user(user).cost(0.0).build());
        } else {
            order = orderRepository.findBySessionIdAndIsConfirmedFalse(sessionId)
                    .orElseGet(() -> Order.builder().sessionId(sessionId).cost(0.0).build());
        }
        return order;
    }
}
