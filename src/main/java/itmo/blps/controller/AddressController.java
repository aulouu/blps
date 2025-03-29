package itmo.blps.controller;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.AddressResponse;
import itmo.blps.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public List<AddressResponse> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @PostMapping("/create-address")
    public AddressResponse createAddress(@RequestBody @Valid AddressRequest addressRequest) {
        return addressService.createAddress(addressRequest);
    }
}
