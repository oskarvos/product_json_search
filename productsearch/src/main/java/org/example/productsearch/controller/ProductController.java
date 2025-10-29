package org.example.productsearch.controller;

import org.example.productsearch.model.Product;
import org.example.productsearch.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/load")
    public ResponseEntity<Product> loadProduct(
            @RequestBody String jsonData,
            @RequestParam String storeName) {
        Product product = productService.saveProductFromJson(jsonData, storeName);
        return ResponseEntity.ok(product);
    }

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
}