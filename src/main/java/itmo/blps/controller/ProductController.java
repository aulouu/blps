package itmo.blps.controller;

import itmo.blps.dto.response.ProductResponse;
import itmo.blps.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/get_all")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/get")
    public ProductResponse getProductById(Long productId) {
        return productService.getProductById(productId);
    }
}
