package itmo.blps.service;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.model.Stock;
import itmo.blps.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockResponse getProductFromStockById(Long id) {
        return stockRepository.findById(id)
                .map(stock -> modelMapper.map(stock, StockResponse.class))
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id %d not found", id)
                ));
    }

    public Page<StockResponse> getFilteredStocks(
            String name,
            Double minPrice,
            Double maxPrice,
            Double minAmount,
            Long restaurantId,
            Pageable pageable) {

        Specification<Stock> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(StockSpecifications.withName(name));
        }
        if (minPrice != null) {
            spec = spec.and(StockSpecifications.withMinPrice(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(StockSpecifications.withMaxPrice(maxPrice));
        }
        if (minAmount != null) {
            spec = spec.and(StockSpecifications.withMinAmount(minAmount));
        }
        if (restaurantId != null) {
            spec = spec.and(StockSpecifications.withRestaurant(restaurantId));
        }

        return stockRepository.findAll(spec, pageable)
                .map(stock -> modelMapper.map(stock, StockResponse.class));
    }
}
