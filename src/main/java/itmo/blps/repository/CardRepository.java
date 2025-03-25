package itmo.blps.repository;

import itmo.blps.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserId(Long userId);

    boolean existsByNumber(String number);

    Optional<Card> findByNumberAndExpiration(String number, String expiration);
}
