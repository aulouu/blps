package itmo.blps.repository;

import itmo.blps.model.Card;
import itmo.blps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumber(String number);

    Optional<Card> findByNumberAndUser(String number, User user);

    Optional<Card> findFirstByUserOrderByIdDesc(User user);
}
