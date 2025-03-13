package itmo.blps.dto.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
@Getter
public class AuthResponseDTO {
    private final String tokenType = "Bearer ";
    private String username;
    private String token;

    public AuthResponseDTO(String username, String token) {
        this.username = username;
        this.token = token;
    }
}