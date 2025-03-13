package itmo.blps.dto.response;

import itmo.blps.model.Address;
import itmo.blps.model.Product;
import itmo.blps.model.Restaurant;
import itmo.blps.model.User;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Double cost;
    private Address address;
    private UserResponse user;
    private List<Product> products;
}
