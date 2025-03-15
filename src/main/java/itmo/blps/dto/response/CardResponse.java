package itmo.blps.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private Long id;
    private String number;
    private String expiration;
    private Double money;
}
