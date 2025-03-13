package itmo.blps.service;

import itmo.blps.dto.response.ProductResponse;
import itmo.blps.exceptions.ProductNotFoundException;
import itmo.blps.model.Product;
import itmo.blps.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductResponse> getAllProducts() {
        List<Product> coordinates = productRepository.findAll();
        return modelMapper.map(coordinates, new TypeToken<List<ProductResponse>>(){}.getType());
    }

    public ProductResponse getProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw  new ProductNotFoundException(
                    String.format("Product with id %d not found", id)
            );
        }
        return modelMapper.map(productRepository.findById(id), ProductResponse.class);
    }
}
