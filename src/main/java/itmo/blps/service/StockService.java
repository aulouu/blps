package itmo.blps.service;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import blps.jca.bitrix24_adapter.Bitrix24ConnectionFactory;
import blps.jca.bitrix24_adapter.Bitrix24Connection;
import itmo.blps.repository.StockRepository;
import jakarta.annotation.Resource;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;
    private final Bitrix24ConnectionFactory bitrix24ConnectionFactory;

//    public StockResponse getProductFromStockById(Long id) {
//        return stockRepository.findById(id)
//                .map(stock -> modelMapper.map(stock, StockResponse.class))
//                .orElseThrow(() -> new ProductNotFoundException(
//                        String.format("Product with id %d not found", id)
//                ));
//    }

    public StockResponse getProductFromBitrix24ById(Long id) {
        log.info("Trying to get product {} from Bitrix24", id);
        try (Bitrix24Connection connection = bitrix24ConnectionFactory.getConnection()) {
            log.info("Connection established, calling API...");

            Map<String, Object> params = new HashMap<>();
            params.put("id", id);

            JsonObject response = connection.callMethod("crm.product.get", params);
            log.info("API response: {}", response);

            if (!response.containsKey("result") || response.isNull("result")) {
                throw new ProductNotFoundException(
                        String.format("Product with id %d not found in Bitrix24", id)
                );
            }

            JsonObject productData = response.getJsonObject("result");

            return StockResponse.builder()
                    .id(safeGet(productData, "ID", Long.class))
                    .name(productData.getString("NAME", ""))
                    .price(safeGet(productData, "PRICE", Double.class))
                    .build();

        } catch (ProductNotFoundException e) {
            throw e;
        } catch (ResourceException e) {
            throw new RuntimeException("Failed to connect to Bitrix24", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing Bitrix24 response", e);
        }
    }

    private <T> T safeGet(JsonObject json, String key, Class<T> type) {
        if (!json.containsKey(key) || json.isNull(key)) {
            return null;
        }

        try {
            JsonValue value = json.get(key);
            String strValue = value.toString().replaceAll("\"", "");

            if (strValue.isEmpty()) {
                return null;
            }

            if (type == Long.class) {
                return type.cast(Long.parseLong(strValue));
            } else if (type == Double.class) {
                return type.cast(Double.parseDouble(strValue));
            }

        } catch (Exception e) {
            log.warn("Error parsing {} value for key: {}", type.getSimpleName(), key, e);
            return null;
        }
        return null;
    }

    public List<StockResponse> getAllProductsFromBitrix24() {
        log.info("Trying to get all products from Bitrix24");
        try (Bitrix24Connection connection = bitrix24ConnectionFactory.getConnection()) {
            log.info("Connection established, calling API...");

            Map<String, Object> params = new HashMap<>();
            JsonObject response = connection.callMethod("crm.product.list", params);
            log.info("API response: {}", response);

            if (!response.containsKey("result") || response.isNull("result")) {
                throw new RuntimeException("No products found in Bitrix24");
            }

            JsonArray products = response.getJsonArray("result");
            List<StockResponse> productList = new ArrayList<>();

            for (JsonObject productData : products.getValuesAs(JsonObject.class)) {
                productList.add(StockResponse.builder()
                        .id(safeGet(productData, "ID", Long.class))
                        .name(productData.getString("NAME", ""))
                        .price(safeGet(productData, "PRICE", Double.class))
                        .build());
            }

            return productList;

        } catch (ResourceException e) {
            log.error("Bitrix24 API error", e);
            throw new RuntimeException("Failed to connect to Bitrix24", e);
        } catch (Exception e) {
            log.error("Bitrix24 API error", e);
            throw new RuntimeException("Error processing Bitrix24 response", e);
        }
    }

//    public Page<StockResponse> getFilteredStocks(
//            String name,
//            Double minPrice,
//            Double maxPrice,
//            Double minAmount,
//            Long restaurantId,
//            Pageable pageable) {
//
//        Specification<Stock> spec = Specification.where(null);
//
//        if (name != null) {
//            spec = spec.and(StockSpecifications.withName(name));
//        }
//        if (minPrice != null) {
//            spec = spec.and(StockSpecifications.withMinPrice(minPrice));
//        }
//        if (maxPrice != null) {
//            spec = spec.and(StockSpecifications.withMaxPrice(maxPrice));
//        }
//        if (minAmount != null) {
//            spec = spec.and(StockSpecifications.withMinAmount(minAmount));
//        }
//        if (restaurantId != null) {
//            spec = spec.and(StockSpecifications.withRestaurant(restaurantId));
//        }
//
//        return stockRepository.findAll(spec, pageable)
//                .map(stock -> modelMapper.map(stock, StockResponse.class));
//    }
}
