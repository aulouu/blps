package itmo.blps.dto.response;

import itmo.blps.model.Address;
import itmo.blps.model.Product;
import lombok.*;

import java.time.LocalTime;
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
    private String deliveryTime;
    private Integer utensilsCount;
}
