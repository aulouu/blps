package itmo.blps.controller;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.AddressResponse;
import itmo.blps.exceptions.UserNotAuthorizedException;
import itmo.blps.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> getAllAddresses() {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return addressService.getAllAddresses();
    }

    @GetMapping("/get-user-addresses")
    public List<AddressResponse> getAllUserAddresses() {
        String username = getCurrentUser();
        if (username == null) {
            throw new UserNotAuthorizedException("User is not authenticated");
        }
        return addressService.getUserAddresses(username);
    }

    @PostMapping("/create-address")
    public AddressResponse createAddress(@RequestBody @Valid AddressRequest addressRequest) {
        return addressService.createAddress(addressRequest);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            return null;
        }
        return userDetails.getUsername();
    }
}
