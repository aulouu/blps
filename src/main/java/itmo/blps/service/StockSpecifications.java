package itmo.blps.service;

import itmo.blps.model.Restaurant;
import itmo.blps.model.Stock;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class StockSpecifications {
    public static Specification<Stock> withName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Stock> withMinPrice(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Stock> withMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Stock> withMinAmount(Double minAmount) {
        return (root, query, cb) ->
                minAmount == null ? null : cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
    }

    public static Specification<Stock> withRestaurant(Long restaurantId) {
        return (root, query, cb) -> {
            if (restaurantId == null) return null;
            Join<Stock, Restaurant> restaurantJoin = root.join("restaurant");
            return cb.equal(restaurantJoin.get("id"), restaurantId);
        };
    }
}
