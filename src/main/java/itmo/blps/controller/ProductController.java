package itmo.blps.controller;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.ErrorResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<StockResponse> products = stockService.getAllProductsFromBitrix24();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error getting products: " + e.getMessage()));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable @Valid String productId) {
        try {
            Long id = Long.parseLong(productId);
            StockResponse response = stockService.getProductFromBitrix24ById(id);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid product ID format"));
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
    }
}
