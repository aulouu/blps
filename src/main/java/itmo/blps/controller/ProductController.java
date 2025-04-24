package itmo.blps.controller;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.ErrorResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final StockService stockService;

//    @GetMapping
//    public List<StockResponse> getProducts(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false) Double minAmount,
//            @RequestParam(required = false) Long restaurantId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "name") String sortField,
//            @RequestParam(defaultValue = "asc") String sortDirection) {
//
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        return stockService.getFilteredStocks(
//                name, minPrice, maxPrice, minAmount, restaurantId, pageable
//        ).getContent();
//    }

//    @GetMapping("/{productId}")
//    public StockResponse getProductById(@PathVariable @Valid String productId) {
//        Long id;
//        try {
//            id = Long.parseLong(productId);
//        } catch (NumberFormatException e) {
//            throw new InvalidRequestException("Invalid product ID format");
//        }
//        return stockService.getProductFromStockById(id);
//    }

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
