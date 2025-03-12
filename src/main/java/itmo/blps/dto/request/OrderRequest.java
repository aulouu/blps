package itmo.blps.dto.request;

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
public class OrderRequest {
    private Double cost;
    private Address address;
    private List<Product> products;
    private List<Restaurant> restaurants;
}
