package itmo.blps.dto.response;

import itmo.blps.model.Address;
import itmo.blps.model.Product;
import itmo.blps.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Double cost;
    private Address address;
    private List<Product> products;
    private List<Restaurant> restaurants;
}
