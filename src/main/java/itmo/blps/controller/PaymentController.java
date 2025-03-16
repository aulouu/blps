package itmo.blps.controller;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.PaymentResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponse payOrder(@RequestBody(required = false) CardRequest cardRequest) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return paymentService.payOrder(username, cardRequest);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        return userDetails.getUsername();
    }
}
