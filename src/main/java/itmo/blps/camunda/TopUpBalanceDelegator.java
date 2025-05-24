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

import java.util.List;
import java.util.stream.Collectors;

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
                    .orElseThrow(() -> new BpmnError("USER_NOT_FOUND", "User details not found for token."));

            String roleName = user.getRole().toString();
            List<String> actualPermissions = user.getRole().getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());

            boolean hasRequiredRole = user.getRole().getPermissions().stream()
                    .map(Enum::name)
                    .peek(permission -> System.out.println("Checking permission: " + permission))
                    .anyMatch("TOP_UP_BALANCE"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: TOP_UP_BALANCE",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
            }

            String number = (String) execution.getVariable("card_number");
            Double money = (Double) execution.getVariable("card_money");

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
