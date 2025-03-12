package itmo.blps.controller;

import itmo.blps.dto.auth.*;
import itmo.blps.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody @Valid RegisterUserDTO registerUserDto, HttpSession httpSession) {
        return authService.register(registerUserDto, httpSession.getId());
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid LoginUserDTO loginUserDto, HttpSession httpSession) {
        return authService.login(loginUserDto, httpSession.getId());
    }
}
