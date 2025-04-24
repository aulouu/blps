package itmo.blps.controller;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.request.ConfirmOrderRequest;
import itmo.blps.dto.request.ProductRequest;
import itmo.blps.dto.response.OrderConfirmationResponse;
import itmo.blps.dto.response.OrderResponse;
import itmo.blps.exceptions.ErrorResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.security.SecurityUtils;
import itmo.blps.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;
    private final SecurityUtils securityUtils;

    @PostMapping("/add-product")
    public ResponseEntity<?> addProductToOrder(@RequestBody @Valid ProductRequest productName, HttpSession httpSession) {
        String username = securityUtils.getCurrentUser();
        try {
            OrderResponse response = orderService.addProductToOrder(productName, httpSession.getId(), username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorMsg = "Error accessing Bitrix24 API";
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause instanceof ProductNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(cause.getMessage()));
            }
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(errorMsg + ": " + cause.getMessage()));
        }
//        return orderService.addProductToOrder(productName, httpSession.getId(), username);
    }

    @PostMapping("/confirm")
    public OrderConfirmationResponse confirmOrder(HttpSession httpSession, @RequestBody @Valid ConfirmOrderRequest confirmOrderRequest) {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.confirmOrder(httpSession.getId(), username, confirmOrderRequest);
    }

    @PostMapping("/set-address")
    public OrderResponse setAddress(@RequestBody @Valid AddressRequest addressRequest) {
        String username = securityUtils.getCurrentUser();
        return orderService.setAddress(addressRequest, httpSession.getId(), username);
    }

    @GetMapping("/get-current")
    public OrderResponse getCurrentOrder(HttpSession httpSession) {
        String username = securityUtils.getCurrentUser();
        return orderService.getCurrentOrder(httpSession.getId(), username);
    }

    @GetMapping("/get-paid-orders")
    public List<OrderResponse> getPaidOrders() {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.getAllPaidOrders();
    }

    @GetMapping("/get-confirmed-orders")
    public List<OrderResponse> getConfirmedOrders() {
        String username = securityUtils.getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return orderService.getAllConfirmedOrders();
    }
}
