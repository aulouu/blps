package itmo.blps.repository;

import itmo.blps.model.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserId(Long userId);
    boolean existsByNumberAndExpirationAndCvvAndMoney(Integer number, Double expiration, Integer cvv, Double money);
    Optional<Card> findByUserIdAndNumberAndExpirationAndCvvAndMoney(Long userId, Integer number, Double expiration, Integer cvv, Double money);
}
