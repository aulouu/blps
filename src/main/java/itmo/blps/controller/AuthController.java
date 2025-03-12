package itmo.blps.controller;

import itmo.blps.dto.auth.*;
import itmo.blps.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody @Valid RegisterUserDTO registerUserDto) {
        return authService.register(registerUserDto);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid LoginUserDTO loginUserDto) {
        return authService.login(loginUserDto);
    }
}
