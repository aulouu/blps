package itmo.blps.dto.request;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private Integer number;
    private Double expiration;
    private Integer cvv;
    private Double money;
}
