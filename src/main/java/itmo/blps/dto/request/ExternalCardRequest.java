package itmo.blps.dto.request;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalCardRequest {
    private String number;
}
