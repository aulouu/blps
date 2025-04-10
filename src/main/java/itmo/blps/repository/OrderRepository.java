package itmo.blps.repository;

import itmo.blps.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstBySessionIdAndIsConfirmedFalseOrderByCreationTimeDesc(String sessionId);

    Optional<Order> findFirstByUserIdAndIsConfirmedFalseOrderByCreationTimeDesc(Long userId);

    Optional<Order> findFirstByUserIdAndIsConfirmedTrueAndIsPaidFalseOrderByCreationTimeDesc(Long userId);

    Optional<List<Order>> findByIsPaidFalse();

    List<Order> findAllByIsPaidTrue();

    List<Order> findAllByIsConfirmedTrue();

    Optional<Order> findFirstByUserIdOrderByCreationTimeDesc(Long userId);

    Optional<Order> findFirstBySessionIdOrderByCreationTimeDesc(String sessionId);
}
