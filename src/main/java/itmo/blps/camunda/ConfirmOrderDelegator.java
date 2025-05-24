package itmo.blps.camunda;

import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.response.OrderConfirmationResponse;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.service.OrderService;
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
public class ConfirmOrderDelegator implements JavaDelegate {
    private final OrderService orderService;
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
                    .anyMatch("CONFIRM_ORDER"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: CONFIRM_ORDER",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
            }

            String deliveryTime = (String) execution.getVariable("delivery_time");
            Integer utensilsCount = (Integer) execution.getVariable("utensils_count");

            if (deliveryTime == null || utensilsCount == null) {
                throw new BpmnError("MISSING_DELIVERY_INFO", "Some delivery fields missing");
            }

            OrderConfirmationResponse order = orderService.confirmOrder("", username, ConfirmOrderRequest.builder()
                    .deliveryTime(deliveryTime)
                    .utensilsCount(utensilsCount)
                    .build());

            execution.setVariable("order_id", order.getId());
            execution.setVariable("order_cost", order.getCost());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
