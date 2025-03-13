package itmo.blps.repository;

import itmo.blps.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findBySessionIdAndIsConfirmedFalse(String sessionId);
    Optional<Order> findByUserIdAndIsConfirmedFalse(Long userId);
}
