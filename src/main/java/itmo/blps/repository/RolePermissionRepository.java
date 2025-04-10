package itmo.blps.repository;

import itmo.blps.model.Permission;
import itmo.blps.model.Role;
import itmo.blps.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Set<RolePermission> findByRole(Role role);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role = ?1")
    Set<Permission> findPermissionsByRole(Role role);

    boolean existsByRoleAndPermission(Role role, Permission permission);
}