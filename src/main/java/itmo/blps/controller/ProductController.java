package itmo.blps.controller;

import itmo.blps.dto.response.ProductResponse;
import itmo.blps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/get_all")
    public List<ProductResponse> getAllAddresses() {
        return productService.getAllProducts();
    }

    // get by id
}
