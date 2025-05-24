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

import java.util.List;
import java.util.stream.Collectors;

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

            String roleName = user.getRole().toString();
            List<String> actualPermissions = user.getRole().getPermissions().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());

            boolean hasRequiredRole = user.getRole().getPermissions().stream()
                    .map(Enum::name)
                    .anyMatch("PAY_ORDER"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: PAY_ORDER",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
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
