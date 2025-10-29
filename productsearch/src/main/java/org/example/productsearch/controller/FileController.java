package org.example.productsearch.controller;

import org.example.productsearch.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping("/process")
    public ResponseEntity<String> processAllFiles() {
        fileProcessingService.processAllJsonFiles();
        return ResponseEntity.ok("All JSON files processed successfully");
    }

    @PostMapping("/process/{storeName}")
    public ResponseEntity<String> processStoreFiles(@PathVariable String storeName) {
        // Можно добавить логику для обработки файлов конкретного магазина
        return ResponseEntity.ok("Files for store " + storeName + " processed successfully");
    }
}