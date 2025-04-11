package itmo.blps.repository;

import itmo.blps.model.Admin;
import itmo.blps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUser(User user);
}
