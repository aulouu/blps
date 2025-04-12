package itmo.blps.controller;

import itmo.blps.dto.request.CardRequest;
import itmo.blps.dto.response.PaymentResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.security.SecurityUtils;
import itmo.blps.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/pay")
    public PaymentResponse payOrder(@RequestBody(required = false) CardRequest cardRequest) {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return paymentService.payOrder(username, cardRequest);
    }
}
