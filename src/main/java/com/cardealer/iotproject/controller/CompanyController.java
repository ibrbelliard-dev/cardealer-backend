package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Company;
import com.cardealer.iotproject.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/company")
@Tag(name = "Company Management", description = "Endpoints for company/dealer information management")
// @CrossOrigin(origins = "http://localhost:8085")
public class CompanyController {
    
    @Autowired
    private CompanyService companyService;

      private final AppConfig appConfig;  // ← AGREGAR

// Agregar constructor
public CompanyController(CompanyService companyService, AppConfig appConfig) {
    this.companyService = companyService;
    this.appConfig = appConfig;
}


    
    @GetMapping
    @Operation(summary = "Get all companies")
    public ResponseEntity<ApiResponse> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("Companies retrieved", companies));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<ApiResponse> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Company retrieved", company));
    }
    
    @GetMapping("/first")
    @Operation(summary = "Get the first company (main dealer info)")
    public ResponseEntity<ApiResponse> getFirstCompany() {
        Company company = companyService.getFirstCompany();
        if (company == null) {
            return ResponseEntity.ok(ApiResponse.success("No company found", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Company retrieved", company));
    }
    
    @GetMapping("/exists")
    @Operation(summary = "Check if company exists")
    public ResponseEntity<ApiResponse> companyExists() {
        boolean exists = companyService.companyExists();
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(ApiResponse.success("Company status retrieved", response));
    }
    
    @PostMapping
    @Operation(summary = "Create a new company")
    public ResponseEntity<ApiResponse> createCompany(@Valid @RequestBody Company company) {
        Company saved = companyService.saveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Company created successfully", saved));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update company information")
    public ResponseEntity<ApiResponse> updateCompany(@PathVariable Long id, @Valid @RequestBody Company company) {
        Company updated = companyService.updateCompany(id, company);
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete company")
    public ResponseEntity<ApiResponse> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted successfully", null));
    }


@PostMapping("/upload-logo")
@Operation(summary = "Upload company logo")
public ResponseEntity<ApiResponse> uploadLogo(@RequestParam("file") MultipartFile file) {
    try {
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please select a file to upload"));
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Only image files are allowed"));
        }
        
        // Validate file size (max 2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File size should be less than 2MB"));
        }
        
        // Create directory if not exists
        Path uploadDir = Paths.get("./uploads/company");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = "logo_" + System.currentTimeMillis() + extension;
        
        // Save file
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the path
        String logoPath = "/uploads/company/" + filename;
        Map<String, String> response = new HashMap<>();
        response.put("logoUrl", logoPath);
        
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully", response));
        
    } catch (IOException e) {
       // log.severe("Failed to upload logo: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Failed to upload logo: " + e.getMessage()));
    }
}
}