package itmo.blps.config;

import itmo.blps.model.Order;
import itmo.blps.repository.OrderRepository;
import itmo.blps.repository.ProductRepository;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@WebListener
public class SessionCleanupListener implements HttpSessionListener {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

//    @Override
//    public void sessionCreated(HttpSessionEvent se) {
//        System.out.println("Session создана: " + se.getSession().getId());
//    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        System.out.println("Сессия завершена: " + sessionId);
        orderRepository.deleteByIsConfirmedFalse();
        orderRepository.deleteByIsPaidFalse();

        List<Order> orders = orderRepository.findByIsConfirmedFalseOrIsPaidFalse()
                .orElse(null);
        if (orders == null) return;
        for (Order order : orders) {
            productRepository.deleteByOrderId(order.getId());
        }

        orderRepository.deleteAll(orders);
    }
}
