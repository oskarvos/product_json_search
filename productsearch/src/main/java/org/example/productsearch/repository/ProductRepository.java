package org.example.productsearch.repository;

import org.example.productsearch.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Поиск по названию товара
    List<Product> findByProductNameContainingIgnoreCase(String productName);

    // Поиск по магазину
    List<Product> findByStoreName(String storeName);

    // Комплексный поиск
    @Query("SELECT p FROM Product p WHERE " +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:minWeight IS NULL OR p.weightGrams >= :minWeight) AND " +
            "(:maxWeight IS NULL OR p.weightGrams <= :maxWeight) AND " +
            "(:fatPercent IS NULL OR p.fatPercent = :fatPercent) AND " +
            "(:productType IS NULL OR LOWER(p.productType) LIKE LOWER(CONCAT('%', :productType, '%'))) AND " +
            "(:available IS NULL OR p.available = :available)")
    List<Product> searchProducts(
            @Param("productName") String productName,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minWeight") Integer minWeight,
            @Param("maxWeight") Integer maxWeight,
            @Param("fatPercent") Double fatPercent,
            @Param("productType") String productType,
            @Param("available") Boolean available);
}
