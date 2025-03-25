package itmo.blps.service;

import itmo.blps.dto.response.StockResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.model.Stock;
import itmo.blps.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public List<StockResponse> getAllProductsOnStock() {
        List<Stock> productsStock = stockRepository.findAll();
        return modelMapper.map(productsStock, new TypeToken<List<StockResponse>>() {
        }.getType());
    }

    public StockResponse getProductFromStockById(Long id) {
        return stockRepository.findById(id)
                .map(stock -> modelMapper.map(stock, StockResponse.class))
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id %d not found", id)
                ));
    }
}
