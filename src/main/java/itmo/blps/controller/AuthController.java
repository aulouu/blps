package itmo.blps.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import itmo.blps.dto.auth.*;
import itmo.blps.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @SecurityRequirements
    public AuthResponseDTO register(@RequestBody @Valid RegisterUserDTO registerUserDto, HttpSession httpSession) {
        return authService.register(registerUserDto, httpSession.getId());
    }

    @PostMapping("/login")
    @SecurityRequirements
    public AuthResponseDTO login(@RequestBody @Valid LoginUserDTO loginUserDto, HttpSession httpSession) {
        return authService.login(loginUserDto, httpSession.getId());
    }
}
