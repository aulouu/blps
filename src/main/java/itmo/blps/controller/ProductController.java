package itmo.blps.controller;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final StockService stockService;

    @GetMapping
    public List<StockResponse> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        Pageable pageable = PageRequest.of(page, size, sort);

        return stockService.getFilteredStocks(
                name, minPrice, maxPrice, minAmount, restaurantId, pageable
        ).getContent();
    }

    @GetMapping("/{productId}")
    public StockResponse getProductById(@RequestBody @Valid Long productId) {
        return stockService.getProductFromStockById(productId);
    }
}
