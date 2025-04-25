package itmo.blps.repository;

import itmo.blps.model.Order;
import itmo.blps.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductNameAndOrder(String productName, Order order);

    Optional<List<Product>> findByOrderId(Long productId);
}
