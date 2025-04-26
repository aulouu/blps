package itmo.blps.service;

import itmo.blps.exceptions.OrderNotFoundException;
import itmo.blps.model.Order;
import itmo.blps.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationListener {
    private final OrderRepository orderRepository;

    @JmsListener(destination = "order.confirmation.queue")
    public void processOrderConfirmation(Long orderId) {
        try {
            log.info("Processing order confirmation for orderId: {}", orderId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
            order.setIsConfirmed(true);
            orderRepository.save(order);
            log.info("Order {} successfully confirmed", orderId);
        } catch (OrderNotFoundException e) {
            log.error("Error processing order confirmation: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing order: " + orderId, e);
            throw new RuntimeException("Order processing failed", e);
        }
    }
}
