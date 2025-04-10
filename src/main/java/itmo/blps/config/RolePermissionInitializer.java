package itmo.blps.config;

import itmo.blps.model.Permission;
import itmo.blps.model.Role;
import itmo.blps.model.RolePermission;
import itmo.blps.repository.RolePermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Component("rolePermissionInitializer")
@RequiredArgsConstructor
@Slf4j
public class RolePermissionInitializer {
    private final RolePermissionRepository rolePermissionRepository;

    @PostConstruct
    @Transactional
    public void initRolePermissions() {
        Map<Role, EnumSet<Permission>> rolePermissions = new HashMap<>();

        rolePermissions.put(Role.UNAUTHORIZED_USER, EnumSet.of(
                Permission.REGISTER,
                Permission.LOGIN,
                Permission.SET_ADDRESS,
                Permission.ADD_PRODUCT,
                Permission.VIEW_CURRENT_ORDER,
                Permission.CREATE_ADDRESS,
                Permission.VIEW_PRODUCT,
                Permission.VIEW_ALL_PRODUCTS));

        rolePermissions.put(Role.USER, EnumSet.of(
                Permission.VIEW_ALL_PRODUCTS,
                Permission.VIEW_PRODUCT,
                Permission.VIEW_CURRENT_ORDER,
                Permission.VIEW_CURRENT_ADDRESSES,
                Permission.PAY_ORDER,
                Permission.CREATE_ADDRESS,
                Permission.SET_ADDRESS,
                Permission.CONFIRM_ORDER,
                Permission.ADD_PRODUCT,
                Permission.CREATE_CARD,
                Permission.TOP_UP_BALANCE));

        rolePermissions.put(Role.ADMIN, EnumSet.of(
                Permission.VIEW_ALL_PAID_ORDERS,
                Permission.VIEW_ALL_CONFIRMED_ORDERS,
                Permission.VIEW_ALL_ADDRESSES,
                Permission.VIEW_ALL_PRODUCTS,
                Permission.VIEW_PRODUCT,
                Permission.VIEW_CURRENT_ORDER,
                Permission.VIEW_CURRENT_ADDRESSES,
                Permission.PAY_ORDER,
                Permission.CREATE_ADDRESS,
                Permission.SET_ADDRESS,
                Permission.CONFIRM_ORDER,
                Permission.ADD_PRODUCT,
                Permission.CREATE_CARD,
                Permission.TOP_UP_BALANCE
        ));

        rolePermissions.forEach((role, permissions) -> {
            permissions.forEach(permission -> {
                if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole(role);
                    rolePermission.setPermission(permission);
                    rolePermissionRepository.save(rolePermission);
                }
            });
        });
    }
}