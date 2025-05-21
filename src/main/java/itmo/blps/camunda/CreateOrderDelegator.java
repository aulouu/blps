package itmo.blps.camunda;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.OrderResponse;
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
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CreateOrderDelegator implements JavaDelegate {
    private final OrderService orderService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String token = (String) execution.getVariable("token");

        try {
            if (token == null || !jwtUtils.validateJwtToken(token)) {
                throw new BpmnError("INVALID_TOKEN", "Authentication token is invalid or missing.");
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
//                        log.error("User not found for username {} from token", username);
                        return new BpmnError("USER_NOT_FOUND", "User details not found for token.");
                    });

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            boolean hasRequiredRole = user.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("VIEW_CURRENT_ORDER"));

            if (!hasRequiredRole) {
                throw new BpmnError("NO_REQUIRED_ROLE", "User does not have required role to create order.");
            }

            String city = (String) execution.getVariable("city");
            String street = (String) execution.getVariable("street");
            Integer building = (Integer) execution.getVariable("building");
            Integer entrance = (Integer) execution.getVariable("entrance");
            Integer flat = (Integer) execution.getVariable("flat");
            Integer floor = (Integer) execution.getVariable("floor");

            if (city == null || street == null || building == null || entrance == null || flat == null || floor == null) {
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
