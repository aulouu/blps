package itmo.blps.camunda;

import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AddProductDelegator implements JavaDelegate {
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
                    .anyMatch("ADD_PRODUCT"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: ADD_PRODUCT",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
            }

            Long productId = (Long) execution.getVariable("product_id");
            Double productCount = ((Long) execution.getVariable("product_count")).doubleValue();

            if (productId == null) {
                throw new BpmnError("MISSING_PRODUCT_INFO", "Some product fields missing");
            }

            OrderResponse order = orderService.addProductToOrder(ProductRequest.builder()
                    .productId(productId)
                    .count(productCount)
                    .build(), "", username);

            execution.setVariable("order_id", order.getId());
            execution.setVariable("order_cost", order.getCost());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
