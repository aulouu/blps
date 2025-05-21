package itmo.blps.dto.response;

import itmo.blps.model.Address;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmationResponse {
    private Long id;
    private Double cost;
    private Address address;
    private UserResponse user;
    private List<ProductResponse> products;
    private String deliveryTime;
    private Integer utensilsCount;
}
