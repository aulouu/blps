package itmo.blps.camunda;

import itmo.blps.dto.request.AddressRequest;
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
public class CreateOrderDelegator implements JavaDelegate {
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
                    .anyMatch("APPROVE_ADMIN_REQUEST"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: APPROVE_ADMIN_REQUEST",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                System.err.println(errorMsg);
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
//                throw new NoRequiredRoleException(errorMsg);
            }

            String city = (String) execution.getVariable("city");
            String street = (String) execution.getVariable("street");
            Integer building = ((Long) execution.getVariable("building")).intValue();
            Integer entrance = ((Long) execution.getVariable("entrance")).intValue();
            Integer flat = ((Long) execution.getVariable("flat")).intValue();
            Integer floor = ((Long) execution.getVariable("floor")).intValue();


            if (city == null || street == null) {
                throw new BpmnError("MISSING_ADDRESS_INFO", "Some address fields missing");
            }

            OrderResponse order = orderService.setAddress(AddressRequest.builder()
                    .building(building)
                    .city(city)
                    .entrance(entrance)
                    .flat(flat)
                    .floor(floor)
                    .street(street)
                    .build(), "", username);

            execution.setVariable("order_id", order.getId());
            execution.setVariable("order_cost", order.getCost());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
