package itmo.blps.controller;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.dto.response.ProductResponse;
import itmo.blps.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private final OrderService orderService;
    private final HttpSession httpSession;

    @PostMapping("/add_product")
    public OrderResponse addProductToOrder(@RequestBody ProductRequest productName, HttpSession httpSession) {
        String username = getCurrentUser();
//        String sessionId = getCurrentSessionId();
        return orderService.addProductToOrder(productName, httpSession.getId(), username);
    }

    @PostMapping("/confirm")
    public OrderResponse confirmOrder(HttpSession httpSession) {
        String username = getCurrentUser();
        return orderService.confirmOrder(httpSession.getId(), username);
    }

    @PostMapping("/set_address")
    public OrderResponse setAddress(@RequestBody @Valid AddressRequest addressRequest) {
        String username = getCurrentUser();
        return orderService.setAddress(addressRequest, httpSession.getId(), username);
    }

    @GetMapping("/get_current")
    public OrderResponse getCurrentOrder(HttpSession httpSession) {
        String username = getCurrentUser();
        return orderService.getCurrentOrder(httpSession.getId(), username);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
//            throw new IllegalStateException("no authentication");
        }
        return userDetails.getUsername();
    }

//    private String getCurrentSessionId() {
//        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                .getRequest().getSession(false);
//        if (session == null) {
//            throw new IllegalStateException("Session not found");
//        }
//        return session.getId();
//    }
}
