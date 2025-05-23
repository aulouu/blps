package itmo.blps.model;

import java.util.Set;

public enum Role {
    USER(Set.of(Permission.VIEW_ALL_PRODUCTS,
            Permission.VIEW_PRODUCT,
            Permission.VIEW_CURRENT_ORDER,
            Permission.VIEW_CURRENT_ADDRESSES,
            Permission.PAY_ORDER,
            Permission.CREATE_ADDRESS,
            Permission.SET_ADDRESS,
            Permission.CONFIRM_ORDER,
            Permission.ADD_PRODUCT,
            Permission.CREATE_CARD,
            Permission.TOP_UP_BALANCE,
            Permission.CREATE_ADMIN_REQUEST)),
    ADMIN(Set.of(Permission.VIEW_ALL_PAID_ORDERS,
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
            Permission.TOP_UP_BALANCE,
            Permission.VIEW_ADMIN_REQUESTS,
            Permission.APPROVE_ADMIN_REQUEST));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
