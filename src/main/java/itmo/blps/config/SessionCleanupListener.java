package itmo.blps.config;

import itmo.blps.model.Order;
import itmo.blps.model.Product;
import itmo.blps.model.Stock;
import itmo.blps.repository.OrderRepository;
import itmo.blps.repository.ProductRepository;
import itmo.blps.repository.StockRepository;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@WebListener
public class SessionCleanupListener implements HttpSessionListener {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
//    private StockRepository stockRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        System.out.println("Session ended: " + sessionId);

        List<Order> orders = orderRepository.findByIsPaidFalse()
                .orElse(null);
        if (orders == null) return;
        for (Order order : orders) {
            List<Product> productsToDelete = productRepository.findByOrderId(order.getId())
                    .orElse(null);
            if (productsToDelete == null) continue;
            //                Stock productOnStock = stockRepository.findById(product.getProductOnStock().getId())
            //                        .orElse(null);
            //                if (productOnStock == null) continue;
            //                productOnStock.setAmount(productOnStock.getAmount() + product.getCount());
            //                stockRepository.save(productOnStock);
            productRepository.deleteAll(productsToDelete);
        }

        orderRepository.deleteAll(orders);
        System.out.println("Session destroyed: " + sessionId);
    }
}
