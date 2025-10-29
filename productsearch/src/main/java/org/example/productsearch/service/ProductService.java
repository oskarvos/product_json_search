package org.example.productsearch.service;

import org.example.productsearch.model.Product;
import org.example.productsearch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JsonParserService jsonParserService;

    public List<Product> searchProducts(String productName, String category,
                                        BigDecimal minPrice, BigDecimal maxPrice,
                                        Integer minWeight, Integer maxWeight,
                                        Double fatPercent, String productType,
                                        Boolean available) {
        try {
            // Для регистронезависимого поиска преобразуем параметры
            String searchProductName = productName != null ? productName.toLowerCase() : null;
            String searchCategory = category != null ? category.toLowerCase() : null;
            String searchProductType = productType != null ? productType.toLowerCase() : null;

            // Получаем все продукты и фильтруем на уровне Java
            List<Product> allProducts = productRepository.findAll();

            return allProducts.stream()
                    .filter(p -> searchProductName == null ||
                            p.getProductName().toLowerCase().contains(searchProductName))
                    .filter(p -> searchCategory == null ||
                            (p.getCategory() != null && p.getCategory().toLowerCase().contains(searchCategory)))
                    .filter(p -> minPrice == null ||
                            (p.getPrice() != null && p.getPrice().compareTo(minPrice) >= 0))
                    .filter(p -> maxPrice == null ||
                            (p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0))
                    .filter(p -> minWeight == null ||
                            (p.getWeightGrams() != null && p.getWeightGrams() >= minWeight))
                    .filter(p -> maxWeight == null ||
                            (p.getWeightGrams() != null && p.getWeightGrams() <= maxWeight))
                    .filter(p -> fatPercent == null ||
                            (p.getFatPercent() != null && p.getFatPercent().equals(fatPercent)))
                    .filter(p -> searchProductType == null ||
                            (p.getProductType() != null && p.getProductType().toLowerCase().contains(searchProductType)))
                    .filter(p -> available == null ||
                            (p.getAvailable() != null && p.getAvailable().equals(available)))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error in searchProducts: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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

    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    public long getProductsCount() {
        return productRepository.count();
    }
}