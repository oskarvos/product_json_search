package org.example.productsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileProcessingService {

    @Autowired
    private ProductService productService;

    private final Path jsonDirectory = Paths.get("json_files");

    public void processAllJsonFiles() {
        try (Stream<Path> paths = Files.walk(jsonDirectory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(this::processJsonFile);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON files", e);
        }
    }

    private void processJsonFile(Path filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(filePath));
            String storeName = extractStoreName(filePath);
            productService.saveProductFromJson(jsonContent, storeName);
            System.out.println("Processed file: " + filePath + " for store: " + storeName);
        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + filePath, e);
        }
    }

    private String extractStoreName(Path filePath) {
        String fileName = filePath.getFileName().toString();
        // Убираем расширение .json и возвращаем название магазина
        return fileName.replace(".json", "");
    }
}
