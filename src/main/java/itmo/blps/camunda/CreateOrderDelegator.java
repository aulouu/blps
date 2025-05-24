package itmo.blps.camunda;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.model.Permission;
import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import itmo.blps.security.jwt.JwtUtils;
import itmo.blps.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreateOrderDelegator implements JavaDelegate {
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

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
                    .anyMatch("SET_ADDRESS"::equals);

            if (!hasRequiredRole) {
                String errorMsg = String.format(
                        "User '%s' with role '%s' lacks required permission. Have: %s, need: SET_ADDRESS",
                        user.getUsername(),
                        roleName,
                        actualPermissions
                );
                throw new BpmnError("NO_REQUIRED_ROLE", errorMsg);
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
