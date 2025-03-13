package itmo.blps.dto.response;

import itmo.blps.model.Restaurant;
import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Double amount;
    private Restaurant restaurant;

}
