package itmo.blps.controller;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final StockService stockService;

    @GetMapping("/get_all")
    public List<StockResponse> getAllProducts() {
        return stockService.getAllProductsOnStock();
    }

    @GetMapping("/get")
    public StockResponse getProductById(Long productId) {
        return stockService.getProductFromStockById(productId);
    }
}
