package itmo.blps.dto.response;

import itmo.blps.model.Stock;
import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Double count;
    private Stock productOnStock;
}
