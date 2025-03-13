package itmo.blps.dto.response;

import lombok.*;

@Data
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
}
