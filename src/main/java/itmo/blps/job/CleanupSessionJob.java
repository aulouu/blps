package itmo.blps.job;

import itmo.blps.model.Order;
import itmo.blps.model.Product;
import itmo.blps.repository.OrderRepository;
import itmo.blps.repository.ProductRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor(force = true)
@Slf4j
public class CleanupSessionJob extends QuartzJobBean {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (orderRepository == null) {
            log.error("orderRepository is null. Autowiring failed?");
            throw new JobExecutionException("orderRepository not injected", false);
        }
        if (productRepository == null) {
            log.error("productRepository is null. Autowiring failed?");
            throw new JobExecutionException("productRepository not injected", false);
        }
        try {
            log.info("Start cleaning up session");
            List<Order> orders = orderRepository.findByIsPaidFalse()
                    .orElse(null);
            if (orders == null) return;
            for (Order order : orders) {
                List<Product> productsToDelete = productRepository.findByOrderId(order.getId())
                        .orElse(null);
                if (productsToDelete == null) continue;
                productRepository.deleteAll(productsToDelete);
            }

            orderRepository.deleteAll(orders);
            log.info("Session cleaned");
        } catch (Exception e) {
            log.error("error while cleaning session job: {}", e.getMessage());
        }
    }
}
