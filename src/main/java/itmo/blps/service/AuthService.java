package itmo.blps.service;

import itmo.blps.repository.UserRepository;
import itmo.blps.dto.auth.*;
import itmo.blps.exceptions.*;
import itmo.blps.model.User;
import itmo.blps.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final OrderService orderService;

    public AuthResponseDTO register(RegisterUserDTO registerUserDto, String sessionId) {
        if (userRepository.existsByUsername(registerUserDto.getUsername()))
            throw new UserAlreadyExistException(
                    String.format("Username %s already exists", registerUserDto.getUsername())
            );

        User user = User
                .builder()
                .username(registerUserDto.getUsername())
                .password(passwordEncoder.encode(registerUserDto.getPassword()))
                .build();

        user = userRepository.save(user);
        String token = jwtUtils.generateJwtToken(user.getUsername());
        orderService.mergeOrder(user.getUsername(), sessionId);

        return new AuthResponseDTO(
                user.getUsername(),
                token
        );
    }

    public AuthResponseDTO login(LoginUserDTO loginUserDto, String sessionId) {
        User user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", loginUserDto.getUsername())
                ));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword())
        );

        String token = jwtUtils.generateJwtToken(user.getUsername());
        orderService.mergeOrder(user.getUsername(), sessionId);

        return new AuthResponseDTO(
                user.getUsername(),
                token
        );
    }
}
