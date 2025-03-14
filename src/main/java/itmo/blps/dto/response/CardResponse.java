package itmo.blps.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private Long id;
    private Integer number;
    private Double expiration;
    private Double money;
}
