package com.cardealer.iotproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cardealer.iotproject.service.FilesStorageService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class FileUploadController {

    @Autowired
    private FilesStorageService filesStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            filesStorageService.save(file);
            String fileName = file.getOriginalFilename();

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", "/uploads/" + fileName);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        }
    }
}