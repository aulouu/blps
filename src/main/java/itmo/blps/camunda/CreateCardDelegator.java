package itmo.blps.camunda;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.CardResponse;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.service.PaymentService;
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
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            String username = (String) execution.getVariable("username");

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BpmnError("USER_NOT_FOUND", "User details not found for token."));

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

            String number = (String) execution.getVariable("card_number");
            String expiration = (String) execution.getVariable("card_expiration");
            String cvv = (String) execution.getVariable("card_cvv");

            if (number == null || expiration == null || cvv == null) {
                throw new BpmnError("MISSING_CARD_INFO", "Some card fields missing");
            }

            CardResponse savedCard = paymentService.createOrGetCard(username, CardRequest.builder()
                    .number(number)
                    .expiration(expiration)
                    .cvv(cvv)
                    .build());

            execution.setVariable("card_id", savedCard.getId());
            execution.setVariable("card_number", savedCard.getNumber());
            execution.setVariable("card_expiration", savedCard.getExpiration());
            execution.setVariable("card_money", savedCard.getMoney());

            Double orderCost = (Double) execution.getVariable("order_cost");
            Boolean enoughMoney = savedCard.getMoney() >= orderCost;
            execution.setVariable("enough_money", enoughMoney);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
