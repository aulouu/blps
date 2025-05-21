package itmo.blps.camunda;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.PaymentResponse;
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
public class PayOrderDelegator implements JavaDelegate {
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
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("PAY_ORDER"));

            if (!hasRequiredRole) {
                throw new BpmnError("NO_REQUIRED_ROLE", "User does not have required role to create card.");
            }

            String number = (String) execution.getVariable("card_number");
            String expiration = (String) execution.getVariable("card_expiration");
            String cvv = (String) execution.getVariable("card_cvv");

            if (number == null || expiration == null || cvv == null) {
                throw new BpmnError("MISSING_PAYMENT_INFO", "Some fields for payment missing");
            }

            PaymentResponse response = paymentService.payOrder(username, CardRequest.builder()
                    .number(number)
                    .expiration(expiration)
                    .cvv(cvv)
                    .build());

            execution.setVariable("payment_message", response.getMessage());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
