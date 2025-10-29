package org.example.productsearch.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "product_name")
    private String productName;

    private String category;
    private BigDecimal price;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "fat_percent")
    private Double fatPercent;

    @Column(name = "product_type")
    private String productType;

    private Boolean available;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String rawData;

    // Конструкторы
    public Product() {}

    public Product(String storeName, String productName, String category,
                   BigDecimal price, Integer weightGrams, Double fatPercent,
                   String productType, Boolean available, String rawData) {
        this.storeName = storeName;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.weightGrams = weightGrams;
        this.fatPercent = fatPercent;
        this.productType = productType;
        this.available = available;
        this.rawData = rawData;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }

    public Double getFatPercent() { return fatPercent; }
    public void setFatPercent(Double fatPercent) { this.fatPercent = fatPercent; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}