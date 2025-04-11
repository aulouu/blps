package itmo.blps.controller;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;

    @PostMapping("/add-product")
    public OrderResponse addProductToOrder(@RequestBody @Valid ProductRequest productName, HttpSession httpSession) {
        String username = getCurrentUser();
        return orderService.addProductToOrder(productName, httpSession.getId(), username);
    }

    @PostMapping("/confirm")
    public OrderResponse confirmOrder(HttpSession httpSession, @RequestBody @Valid ConfirmOrderRequest confirmOrderRequest) {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.confirmOrder(httpSession.getId(), username, confirmOrderRequest);
    }

    @PostMapping("/set-address")
    public OrderResponse setAddress(@RequestBody @Valid AddressRequest addressRequest) {
        String username = getCurrentUser();
        return orderService.setAddress(addressRequest, httpSession.getId(), username);
    }

    @GetMapping("/get-current")
    public OrderResponse getCurrentOrder(HttpSession httpSession) {
        String username = getCurrentUser();
        return orderService.getCurrentOrder(httpSession.getId(), username);
    }

    @GetMapping("/get-paid-orders")
    public List<OrderResponse> getPaidOrders() {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.getAllPaidOrders();
    }

    @GetMapping("/get-confirmed-orders")
    public List<OrderResponse> getConfirmedOrders() {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.getAllConfirmedOrders();
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        return userDetails.getUsername();
    }
}
