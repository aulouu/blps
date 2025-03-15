package itmo.blps.dto.response;

import itmo.blps.model.Order;
import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String message;
    private OrderResponse order;
}
