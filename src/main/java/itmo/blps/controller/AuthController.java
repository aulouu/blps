package itmo.blps.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import itmo.blps.dto.auth.AuthResponseDTO;
import itmo.blps.dto.auth.LoginUserDTO;
import itmo.blps.dto.auth.RegisterUserDTO;
import itmo.blps.model.User;
import itmo.blps.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/check_auth")
    public AuthResponseDTO checkAuth(@AuthenticationPrincipal User user) {
        return authService.checkAuth(user);
    }

    @PostMapping("/invalidate_session")
    public String invalidateSession(HttpSession session) {
        session.invalidate();
        return "Session invalidated";
    }
}
