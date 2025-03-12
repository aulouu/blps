package itmo.blps.dto.auth;

import lombok.Getter;

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