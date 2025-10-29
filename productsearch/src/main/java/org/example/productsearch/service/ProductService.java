package org.example.productsearch.service;

import org.example.productsearch.model.Product;
import org.example.productsearch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JsonParserService jsonParserService;

    public List<Product> saveProductsFromJson(String jsonData, String storeName) {
        List<Product> products = jsonParserService.parseJsonToProducts(jsonData, storeName);
        return productRepository.saveAll(products);
    }

    // Для обратной совместимости оставляем старый метод
    public Product saveProductFromJson(String jsonData, String storeName) {
        List<Product> products = saveProductsFromJson(jsonData, storeName);
        // Возвращаем первый продукт (основной) для обратной совместимости
        return products.isEmpty() ? null : products.get(0);
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

    public void processAndSaveJsonFile(String filePath, String storeName) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            saveProductsFromJson(jsonContent, storeName);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }
}