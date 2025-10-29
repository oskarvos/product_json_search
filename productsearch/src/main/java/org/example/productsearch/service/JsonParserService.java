package org.example.productsearch.service;

import org.example.productsearch.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> parseJsonToProducts(String jsonData, String storeName) {
        List<Product> products = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);

            // Парсим основной продукт из menu_item
            if (rootNode.has("menu_item")) {
                Product mainProduct = parseMenuItem(rootNode.get("menu_item"), storeName);
                if (mainProduct != null) {
                    products.add(mainProduct);
                }
            }

            // Парсим рекомендуемые продукты из detailed_data
            if (rootNode.has("detailed_data")) {
                List<Product> recommendedProducts = parseRecommendedProducts(rootNode.get("detailed_data"), storeName);
                products.addAll(recommendedProducts);
            }

            return products;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    private Product parseMenuItem(JsonNode menuItem, String storeName) {
        Product product = new Product();
        product.setStoreName(storeName);

        // Извлекаем название
        if (menuItem.has("name")) {
            product.setProductName(menuItem.get("name").asText());
        }

        // Извлекаем описание
        String description = "";
        if (menuItem.has("description")) {
            description = menuItem.get("description").asText();
        }

        // Извлекаем цену - используем decimalPrice если есть, иначе price
        if (menuItem.has("decimalPrice")) {
            String priceStr = menuItem.get("decimalPrice").asText();
            try {
                product.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                // Если decimalPrice не число, пробуем обычный price
                if (menuItem.has("price")) {
                    product.setPrice(BigDecimal.valueOf(menuItem.get("price").asDouble()));
                }
            }
        } else if (menuItem.has("price")) {
            product.setPrice(BigDecimal.valueOf(menuItem.get("price").asDouble()));
        }

        // Извлекаем вес
        if (menuItem.has("weight")) {
            String weightStr = menuItem.get("weight").asText();
            product.setWeightGrams(extractWeightInGrams(weightStr));
        }

        // Определяем категорию и тип из названия и описания
        determineCategoryAndType(product, description);

        // Извлекаем доступность
        if (menuItem.has("available")) {
            product.setAvailable(menuItem.get("available").asBoolean());
        } else {
            product.setAvailable(true); // по умолчанию true
        }

        // Для основного продукта сохраняем полный JSON
        product.setRawData(menuItem.toString());

        return product;
    }

    private List<Product> parseRecommendedProducts(JsonNode detailedData, String storeName) {
        List<Product> products = new ArrayList<>();
        if (detailedData.isArray()) {
            for (JsonNode item : detailedData) {
                if (item.has("type") && "separator".equals(item.get("type").asText())) {
                    // Пропускаем разделители
                    continue;
                }
                if (item.has("payload") && item.get("payload").has("recommendations")) {
                    JsonNode recommendations = item.get("payload").get("recommendations");
                    if (recommendations.isArray()) {
                        for (JsonNode recommendation : recommendations) {
                            if (recommendation.has("items")) {
                                JsonNode items = recommendation.get("items");
                                if (items.isArray()) {
                                    for (JsonNode recommendedItem : items) {
                                        Product product = parseRecommendedItem(recommendedItem, storeName);
                                        if (product != null) {
                                            products.add(product);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return products;
    }

    private Product parseRecommendedItem(JsonNode item, String storeName) {
        Product product = new Product();
        product.setStoreName(storeName);

        if (item.has("name")) {
            product.setProductName(item.get("name").asText());
        }

        String description = "";
        if (item.has("description")) {
            description = item.get("description").asText();
        }

        // Извлекаем цену
        if (item.has("decimalPrice")) {
            String priceStr = item.get("decimalPrice").asText();
            try {
                product.setPrice(new BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                if (item.has("price")) {
                    product.setPrice(BigDecimal.valueOf(item.get("price").asDouble()));
                }
            }
        } else if (item.has("price")) {
            product.setPrice(BigDecimal.valueOf(item.get("price").asDouble()));
        }

        // Извлекаем вес
        if (item.has("weight")) {
            String weightStr = item.get("weight").asText();
            product.setWeightGrams(extractWeightInGrams(weightStr));
        }

        determineCategoryAndType(product, description);

        if (item.has("available")) {
            product.setAvailable(item.get("available").asBoolean());
        } else {
            product.setAvailable(true);
        }

        // Для рекомендуемых продуктов также сохраняем JSON
        product.setRawData(item.toString());

        return product;
    }

    private Integer extractWeightInGrams(String weightStr) {
        if (weightStr == null || weightStr.trim().isEmpty()) return null;

        try {
            // Удаляем все нецифровые символы кроме точки и запятой
            String cleaned = weightStr.replaceAll("[^0-9,.]", "").replace(",", ".");

            if (!cleaned.isEmpty()) {
                double weight = Double.parseDouble(cleaned);

                // Если в строке есть указание на кг, умножаем на 1000
                if (weightStr.toLowerCase().contains("кг") || weightStr.toLowerCase().contains("kg")) {
                    return (int) (weight * 1000);
                }
                return (int) weight;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing weight: " + weightStr);
        }
        return null;
    }

    private void determineCategoryAndType(Product product, String description) {
        String name = product.getProductName().toLowerCase();
        String desc = description.toLowerCase();

        // Определяем категорию на основе названия и описания
        if (name.contains("прокладк") || desc.contains("прокладк")) {
            product.setCategory("Женская гигиена");
            if (name.contains("ежедневн") || desc.contains("ежедневн")) {
                product.setProductType("Ежедневные прокладки");
            } else {
                product.setProductType("Прокладки");
            }
        } else if (name.contains("тампон") || desc.contains("тампон")) {
            product.setCategory("Женская гигиена");
            product.setProductType("Тампоны");
        } else if (name.contains("молок") || desc.contains("молок")) {
            product.setCategory("Молочные продукты");
            if (name.contains("сгущен")) {
                product.setProductType("Сгущенное молоко");
            }
            product.setFatPercent(extractFatPercent(name + " " + desc));
        } else if (name.contains("сметан") || desc.contains("сметан")) {
            product.setCategory("Молочные продукты");
            product.setProductType("Сметана");
            product.setFatPercent(extractFatPercent(name + " " + desc));
        } else if (name.contains("хлеб") || desc.contains("хлеб")) {
            product.setCategory("Хлебобулочные изделия");
            if (name.contains("ржаной") || desc.contains("ржаной")) {
                product.setProductType("Ржаной хлеб");
            }
        } else {
            product.setCategory("Другое");
        }
    }

    private Double extractFatPercent(String text) {
        // Ищем паттерн типа "15%", "20% жирности" и т.д.
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+[,.]?\\d*)\\s*%");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1).replace(",", "."));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}