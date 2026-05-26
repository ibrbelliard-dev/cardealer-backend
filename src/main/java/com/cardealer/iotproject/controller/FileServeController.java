package com.cardealer.iotproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.cardealer.iotproject.service.FilesStorageService;

@RestController
public class FileServeController {

    @Autowired
    private FilesStorageService filesStorageService;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource resource = filesStorageService.load(filename);
            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/uploads/{folder}/{filename:.+}")
    public ResponseEntity<Resource> serveFileInFolder(
            @PathVariable String folder, 
            @PathVariable String filename) {
        try {
            String fullPath = folder + "/" + filename;
            Resource resource = filesStorageService.load(fullPath);
            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (filename.endsWith(".png")) {
            return "image/png";
        }
        if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        if (filename.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}