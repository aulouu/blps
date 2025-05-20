package itmo.blps.camunda;

import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderConfirmationResponse;
import itmo.blps.dto.response.OrderResponse;
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
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADD_PRODUCT"));

            if (!hasRequiredRole) {
                throw new BpmnError("NO_REQUIRED_ROLE", "User does not have required role to create order.");
            }


            String deliveryTime = (String) execution.getVariable("delivery_time");
            Integer utensilsCount = (Integer) execution.getVariable("utensils_count");

            if (deliveryTime == null || utensilsCount == null) {
                throw new BpmnError("MISSING_PRODUCT_INFO", "Some product fields missing");
            }

            OrderConfirmationResponse order = orderService.confirmOrder("", username, ConfirmOrderRequest.builder()
                            .deliveryTime(deliveryTime)
                            .utensilsCount(utensilsCount)
                    .build());

            execution.setVariable("order_id", order.getId());
            execution.setVariable("order_cost", order.getCost());
            execution.setVariable("order_status", order.getStatus());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
