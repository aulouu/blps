package itmo.blps.camunda;

import itmo.blps.model.Role;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChecker implements JavaDelegate {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String token = (String) delegateExecution.getVariable("token");
        if (token == null) {
            delegateExecution.setVariable("error", "token is null");
            throw new BpmnError("token is null");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            delegateExecution.setVariable("error", "token is invalid");
            throw new BpmnError("invalid token");
        }

        User user = userRepository.findByUsername(jwtUtils.getUserNameFromJwtToken(token))
                .orElseThrow(() -> {
                    delegateExecution.setVariable("error", "user not found");
                    return new BpmnError("user not found");
                });
        delegateExecution.setVariable("username", user.getUsername());

        boolean hasRoleUnauthorized = false;
        boolean hasRoleUser = false;
        boolean hasRoleAdmin = false;
        if (user.getRole() == Role.UNAUTHORIZED_USER)
            hasRoleUnauthorized = true;
        if (user.getRole() == Role.USER)
            hasRoleUser = true;
        if (user.getRole() == Role.ADMIN)
            hasRoleAdmin = true;

        delegateExecution.setVariable("hasRoleUnauthorized", hasRoleUnauthorized);
        delegateExecution.setVariable("hasRoleUser", hasRoleUser);
        delegateExecution.setVariable("hasRoleAdmin", hasRoleAdmin);
        System.out.println(hasRoleUnauthorized);
    }

}

