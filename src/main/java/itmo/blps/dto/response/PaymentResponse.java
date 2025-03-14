package itmo.blps.dto.response;

import itmo.blps.model.Card;
import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String message;
    private Card card;
}
