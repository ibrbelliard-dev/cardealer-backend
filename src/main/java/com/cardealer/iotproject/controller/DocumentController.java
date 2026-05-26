package com.cardealer.iotproject.controller;

import com.cardealer.iotproject.model.dto.ApiResponse;
import com.cardealer.iotproject.model.entity.Document;
import com.cardealer.iotproject.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cardealer.iotproject.config.AppConfig;  // ← AGREGAR

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "Endpoints for document and media management")
// @CrossOrigin(origins = "http://localhost:8085")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
     private final AppConfig appConfig;  // ← AGREGAR
    
    // ← AGREGAR constructor
    public DocumentController(DocumentService documentService, AppConfig appConfig) {
        this.documentService = documentService;
        this.appConfig = appConfig;
    }
    @PostMapping("/upload")
    @Operation(summary = "Upload a document")
    public ResponseEntity<ApiResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String category,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "system") String uploadedBy,
            @RequestParam(required = false) Boolean isTemplate,
            @RequestParam(required = false) String relatedEntityType,
            @RequestParam(required = false) Long relatedEntityId) {
        
        try {
            Document document = documentService.uploadDocument(file, title, description, category, tags, uploadedBy, isTemplate, relatedEntityType, relatedEntityId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Documento subido exitosamente", document));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al subir documento: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all documents")
    public ResponseEntity<ApiResponse> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        Page<Document> documents;
        if (search != null && !search.isEmpty()) {
            documents = documentService.searchDocuments(search, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt")));
        } else {
            documents = documentService.getAllDocuments(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedAt")));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Documentos recuperados", documents));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<ApiResponse> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        documentService.incrementAccessCount(id);
        return ResponseEntity.ok(ApiResponse.success("Documento recuperado", document));
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all document categories")
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<String> categories = documentService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías recuperadas", categories));
    }
    
    @GetMapping("/templates")
    @Operation(summary = "Get all templates")
    public ResponseEntity<ApiResponse> getTemplates() {
        List<Document> templates = documentService.getTemplates();
        return ResponseEntity.ok(ApiResponse.success("Plantillas recuperadas", templates));
    }
    
    @GetMapping("/by-category/{category}")
    @Operation(summary = "Get documents by category")
    public ResponseEntity<ApiResponse> getDocumentsByCategory(@PathVariable String category) {
        List<Document> documents = documentService.getDocumentsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Documentos recuperados", documents));
    }
    
    @GetMapping("/download/{id}")
    @Operation(summary = "Download document file")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            Path filePath = documentService.getDocumentPath(document);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            documentService.incrementAccessCount(id);
            
            String encodedFileName = URLEncoder.encode(document.getOriginalFileName(), StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/view/{id}")
    @Operation(summary = "View document (preview in browser)")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            Path filePath = documentService.getDocumentPath(document);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            documentService.incrementAccessCount(id);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getOriginalFileName() + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update document metadata")
    public ResponseEntity<ApiResponse> updateDocument(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags) {
        
        try {
            Document updated = documentService.updateDocument(id, title, description, category, tags);
            return ResponseEntity.ok(ApiResponse.success("Documento actualizado", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al actualizar documento: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/version")
    @Operation(summary = "Upload new version of a document")
    public ResponseEntity<ApiResponse> uploadNewVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String versionNotes) {
        
        try {
            Document newVersion = documentService.createNewVersion(id, file, versionNotes);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Nueva versión creada", newVersion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al crear nueva versión: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document")
    public ResponseEntity<ApiResponse> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(ApiResponse.success("Documento eliminado exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al eliminar documento: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get document statistics")
    public ResponseEntity<ApiResponse> getDocumentStats() {
        List<Document> allDocs = documentService.getAllDocuments(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", allDocs.size());
        stats.put("totalTemplates", allDocs.stream().filter(Document::getIsTemplate).count());
        stats.put("totalSize", allDocs.stream().mapToLong(Document::getFileSize).sum());
        
        Map<String, Long> byCategory = new HashMap<>();
        for (Document doc : allDocs) {
            byCategory.merge(doc.getCategory(), 1L, Long::sum);
        }
        stats.put("byCategory", byCategory);
        
        return ResponseEntity.ok(ApiResponse.success("Estadísticas de documentos", stats));
    }
}