package org.example.productsearch.service;

import org.example.productsearch.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileProcessingService {

    @Autowired
    private ProductService productService;

    // Исправьте путь на правильный
    private final Path jsonDirectory = Paths.get("json_file"); // или "json_files" в зависимости от вашей структуры

    public void processAllJsonFiles() {
        try {
            // Проверяем существование директории
            if (!Files.exists(jsonDirectory)) {
                System.err.println("Directory does not exist: " + jsonDirectory.toAbsolutePath());
                return;
            }

            System.out.println("Processing files from: " + jsonDirectory.toAbsolutePath());

            try (Stream<Path> paths = Files.walk(jsonDirectory)) {
                long fileCount = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .peek(path -> System.out.println("Found file: " + path))
                        .count();

                System.out.println("Total JSON files found: " + fileCount);
            }

            // Обрабатываем файлы
            try (Stream<Path> paths = Files.walk(jsonDirectory)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(this::processJsonFile);

                System.out.println("All files processed successfully");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON files from: " + jsonDirectory, e);
        }
    }

    private void processJsonFile(Path filePath) {
        try {
            System.out.println("Processing file: " + filePath);
            String jsonContent = new String(Files.readAllBytes(filePath));
            String storeName = extractStoreName(filePath);

            List<Product> products = productService.saveProductsFromJson(jsonContent, storeName);

            System.out.println("Successfully processed: " + filePath +
                    " | Store: " + storeName +
                    " | Products saved: " + products.size());

            for (Product product : products) {
                System.out.println("  - " + product.getProductName() + " | Price: " + product.getPrice());
            }

        } catch (Exception e) {
            System.err.println("Error processing file: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractStoreName(Path filePath) {
        String fileName = filePath.getFileName().toString();
        return fileName.replace(".json", "");
    }
}