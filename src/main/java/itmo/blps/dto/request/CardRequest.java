package itmo.blps.dto.request;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private String number;
    private String expiration;
    private String cvv;
    private Double money;
}
