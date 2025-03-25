package itmo.blps.controller;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final StockService stockService;

    @GetMapping()
    public List<StockResponse> getAllProducts() {
        return stockService.getAllProductsOnStock();
    }

    @GetMapping("/{productId}")
    public StockResponse getProductById(@PathVariable Long productId) {
        return stockService.getProductFromStockById(productId);
    }
}
