package itmo.blps.camunda;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.jwt.JwtUtils;
import itmo.blps.service.CardService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCardDelegator implements JavaDelegate {
    private final CardService cardService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String token = (String) execution.getVariable("token");

        try {
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                throw new BpmnError("INVALID_TOKEN", "Authentication token is invalid or missing.");
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
//                        log.error("User not found for username {} from token", username);
                        return new BpmnError("USER_NOT_FOUND", "User details not found for token.");
                    });

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            boolean hasRequiredRole = user.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("CREATE_CARD"));

            if (!hasRequiredRole) {
                throw new BpmnError("NO_REQUIRED_ROLE", "User does not have required role to create card.");
            }

            String number = (String) execution.getVariable("number");
            String expiration = (String) execution.getVariable("expiration");
            String cvv = (String) execution.getVariable("cvv");

            if (number == null || expiration == null || cvv == null) {
                throw new BpmnError("MISSING_CARD_INFO", "Some card fields missing");
            }

            CardResponse savedCard = cardService.createCard(CardRequest.builder()
                    .number(number)
                    .expiration(expiration)
                    .cvv(cvv)
                    .build(), username);

            execution.setVariable("card_id", savedCard.getId());
            execution.setVariable("card_number", savedCard.getNumber());
            execution.setVariable("card_expiration", savedCard.getExpiration());
            execution.setVariable("card_money", savedCard.getMoney());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
