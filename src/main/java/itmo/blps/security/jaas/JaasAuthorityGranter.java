package itmo.blps.security.jaas;

import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JaasAuthorityGranter implements AuthorityGranter {
    private final UserRepository userRepository;

    @Override
    public Set<String> grant(Principal principal) {
        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        return user.getRole().getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
