package org.example.productsearch.controller;

import org.example.productsearch.model.Product;
import org.example.productsearch.repository.ProductRepository;
import org.example.productsearch.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minWeight,
            @RequestParam(required = false) Integer maxWeight,
            @RequestParam(required = false) Double fatPercent,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) Boolean available) {

        List<Product> products = productService.searchProducts(
                productName, category, minPrice, maxPrice,
                minWeight, maxWeight, fatPercent, productType, available
        );

        return ResponseEntity.ok(products);
    }

    @PostMapping("/load")
    public ResponseEntity<Product> loadProduct(
            @RequestBody String jsonData,
            @RequestParam String storeName) {
        Product product = productService.saveProductFromJson(jsonData, storeName);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/store/{storeName}")
    public ResponseEntity<List<Product>> getProductsByStore(@PathVariable String storeName) {
        List<Product> products = productService.getProductsByStore(storeName);
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllProducts() {
        long countBefore = productService.getProductsCount();
        productService.deleteAllProducts();
        long countAfter = productService.getProductsCount();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Database cleared successfully");
        response.put("deletedCount", countBefore);
        response.put("remainingCount", countAfter);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getProductsCount() {
        long count = productService.getProductsCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String productName) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(productName);
        return ResponseEntity.ok(products);
    }

    // Простой поиск по категории
    @GetMapping("/search/by-category")
    public ResponseEntity<List<Product>> searchByCategory(@RequestParam String category) {
        List<Product> products = productRepository.findByCategoryContainingIgnoreCase(category);
        return ResponseEntity.ok(products);
    }

    // Комбинированный поиск по названию и категории
    @GetMapping("/search/simple")
    public ResponseEntity<List<Product>> simpleSearch(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category) {

        List<Product> products;
        if (productName != null && category != null) {
            // Фильтруем вручную
            List<Product> byName = productRepository.findByProductNameContainingIgnoreCase(productName);
            products = byName.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().toLowerCase().contains(category.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (productName != null) {
            products = productRepository.findByProductNameContainingIgnoreCase(productName);
        } else if (category != null) {
            products = productRepository.findByCategoryContainingIgnoreCase(category);
        } else {
            products = productService.getAllProducts();
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/in-store")
    public ResponseEntity<List<Product>> searchInStore(
            @RequestParam String storeName,
            @RequestParam(required = false) String productName) {

        List<Product> products;
        if (productName != null) {
            products = productRepository.findByStoreNameAndProductNameContainingIgnoreCase(storeName, productName);
        } else {
            products = productRepository.findByStoreName(storeName);
        }

        return ResponseEntity.ok(products);
    }

}