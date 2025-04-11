package itmo.blps.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private Long id;
    private UserResponse user;
}
