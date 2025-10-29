package org.example.productsearch.service;

import org.example.productsearch.model.Product;
import org.example.productsearch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JsonParserService jsonParserService;

    public Product saveProductFromJson(String jsonData, String storeName) {
        Product product = jsonParserService.parseJsonToProduct(jsonData, storeName);
        return productRepository.save(product);
    }

    public List<Product> searchProducts(String productName, String category,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        Integer minWeight, Integer maxWeight,
                                        Double fatPercent, String productType,
                                        Boolean available) {
        return productRepository.searchProducts(
                productName, category, minPrice, maxPrice,
                minWeight, maxWeight, fatPercent, productType, available
        );
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByStore(String storeName) {
        return productRepository.findByStoreName(storeName);
    }
}