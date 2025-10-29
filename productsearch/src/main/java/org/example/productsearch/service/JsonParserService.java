package org.example.productsearch.service;


import org.example.productsearch.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class JsonParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Product parseJsonToProduct(String jsonData, String storeName) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            Product product = new Product();
            product.setStoreName(storeName);
            product.setRawData(jsonData);

            // Парсим в зависимости от структуры JSON
            if (rootNode.has("menu_item")) {
                parseMenuItemFormat(rootNode, product);
            } else if (rootNode.has("products")) {
                parseProductsFormat(rootNode, product);
            } else if (rootNode.has("name")) {
                parseSimpleFormat(rootNode, product);
            }

            return product;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    private void parseMenuItemFormat(JsonNode rootNode, Product product) {
        JsonNode menuItem = rootNode.get("menu_item");

        // Извлекаем название
        if (menuItem.has("name")) {
            product.setProductName(menuItem.get("name").asText());
        }

        // Извлекаем цену
        if (menuItem.has("decimalPrice")) {
            String priceStr = menuItem.get("decimalPrice").asText();
            try {
                product.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                // Если не число, пробуем извлечь из другого поля
                if (menuItem.has("price")) {
                    product.setPrice(BigDecimal.valueOf(menuItem.get("price").asDouble()));
                }
            }
        }

        // Извлекаем вес
        if (menuItem.has("weight")) {
            String weightStr = menuItem.get("weight").asText();
            product.setWeightGrams(extractWeightInGrams(weightStr));
        }

        // Определяем категорию и тип из названия
        determineCategoryAndType(product);

        // Извлекаем доступность
        if (menuItem.has("available")) {
            product.setAvailable(menuItem.get("available").asBoolean());
        }
    }

    private void parseProductsFormat(JsonNode rootNode, Product product) {
        // Аналогичная логика для другого формата
        JsonNode products = rootNode.get("products");
        if (products.isArray() && products.size() > 0) {
            JsonNode firstProduct = products.get(0);
            // Парсим поля...
        }
    }

    private void parseSimpleFormat(JsonNode rootNode, Product product) {
        // Парсим простой формат
        if (rootNode.has("name")) {
            product.setProductName(rootNode.get("name").asText());
        }
        // ... остальные поля
    }

    private Integer extractWeightInGrams(String weightStr) {
        if (weightStr == null) return null;

        // Удаляем все нецифровые символы и преобразуем в граммы
        String digits = weightStr.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            int weight = Integer.parseInt(digits);

            // Если в названии есть "кг" или "kg", умножаем на 1000
            if (weightStr.toLowerCase().contains("кг") || weightStr.toLowerCase().contains("kg")) {
                return weight * 1000;
            }
            return weight;
        }
        return null;
    }

    private void determineCategoryAndType(Product product) {
        String name = product.getProductName().toLowerCase();

        // Определяем категорию
        if (name.contains("сметан")) {
            product.setCategory("Молочные");
            product.setFatPercent(extractFatPercent(name));
        } else if (name.contains("молок")) {
            product.setCategory("Молочные");
            if (name.contains("сгущен")) {
                product.setProductType("сгущенное");
            }
        } else if (name.contains("хлеб")) {
            product.setCategory("Хлебобулочные");
            if (name.contains("ржаной")) {
                product.setProductType("ржаной");
            }
        }
        // ... другие категории
    }

    private Double extractFatPercent(String productName) {
        // Извлекаем жирность из названия
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)[%]");
        java.util.regex.Matcher matcher = pattern.matcher(productName);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }
}