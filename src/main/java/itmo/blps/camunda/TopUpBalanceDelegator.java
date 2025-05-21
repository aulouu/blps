package itmo.blps.camunda;

import itmo.blps.dto.request.BalanceRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
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
public class TopUpBalanceDelegator implements JavaDelegate {
    private final CardService cardService;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            String username = (String) execution.getVariable("username");

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        return new BpmnError("USER_NOT_FOUND", "User details not found for token.");
                    });

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            boolean hasRequiredRole = user.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("TOP_UP_BALANCE"));

            if (!hasRequiredRole) {
                throw new BpmnError("NO_REQUIRED_ROLE", "User does not have required role to top up balance.");
            }

            String number = (String) execution.getVariable("number");
            Double money = (Double) execution.getVariable("money");

            if (number == null || money == null) {
                throw new BpmnError("MISSING_TOP_UP_BALANCE_INFO", "Some 'top up balance' fields missing");
            }

            CardResponse updatedCard = cardService.topUpBalance(BalanceRequest.builder()
                    .number(number)
                    .money(money)
                    .build(), username);

            execution.setVariable("card_id", updatedCard.getId());
            execution.setVariable("card_number", updatedCard.getNumber());
            execution.setVariable("card_expiration", updatedCard.getExpiration());
            execution.setVariable("card_money", updatedCard.getMoney());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
