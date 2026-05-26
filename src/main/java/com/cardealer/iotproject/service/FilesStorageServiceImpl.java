package com.cardealer.iotproject.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  //  private final Path root = Paths.get("uploads");
    private final Path root = Paths.get("/home/ibrbelliard/uploads");

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename).normalize();
            
            // Security check: ensure file is inside uploads directory
            if (!file.startsWith(root)) {
                throw new RuntimeException("Cannot access file outside uploads directory");
            }
            
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1)
                .filter(path -> !path.equals(this.root))
                .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
public String storeFile(MultipartFile file) {
    try {
        String originalFileName = file.getOriginalFilename();
        // Limpiar el nombre del archivo
        originalFileName = originalFileName.replaceAll("[! #]", "");
        
        Files.copy(file.getInputStream(), this.root.resolve(originalFileName));
        return originalFileName;
    } catch (Exception e) {
        if (e instanceof FileAlreadyExistsException) {
            throw new RuntimeException("A file of that name already exists.");
        }
        throw new RuntimeException(e.getMessage());
    }
}
}