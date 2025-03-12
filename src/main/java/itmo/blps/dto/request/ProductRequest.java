package itmo.blps.dto.request;

import itmo.blps.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private Double price;
    private Double amount;
    private Restaurant restaurant;
}
