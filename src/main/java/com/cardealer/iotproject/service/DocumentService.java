package com.cardealer.iotproject.service;

import com.cardealer.iotproject.model.entity.Document;
import com.cardealer.iotproject.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DocumentService {
    
    private static final Logger log = Logger.getLogger(DocumentService.class.getName());
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    private static final String DOCUMENTS_FOLDER = "documents";
    
    @Transactional
    public Document uploadDocument(MultipartFile file, String title, String description, 
                                    String category, String tags, String uploadedBy,
                                    Boolean isTemplate, String relatedEntityType, Long relatedEntityId) throws IOException {
        
        // Create documents directory if not exists
        Path documentsPath = Paths.get(uploadDir, DOCUMENTS_FOLDER);
        if (!Files.exists(documentsPath)) {
            Files.createDirectories(documentsPath);
        }
        
        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = documentsPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Determine document type
        String documentType = getDocumentType(originalFileName);
        
        // Create document entity
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setCategory(category);
        document.setDocumentType(documentType);
        document.setFileName(fileName);
        document.setOriginalFileName(originalFileName);
        document.setFilePath("/uploads/documents/" + fileName);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setTags(tags);
        document.setUploadedBy(uploadedBy);
        document.setIsTemplate(isTemplate != null ? isTemplate : false);
        document.setRelatedEntityType(relatedEntityType);
        document.setRelatedEntityId(relatedEntityId);
        
        Document saved = documentRepository.save(document);
        log.info("Document uploaded: " + title + " by " + uploadedBy);
        
        return saved;
    }
    
    @Transactional
    public Document updateDocument(Long id, String title, String description, String category, String tags) {
        Document document = getDocumentById(id);
        
        if (title != null) document.setTitle(title);
        if (description != null) document.setDescription(description);
        if (category != null) document.setCategory(category);
        if (tags != null) document.setTags(tags);
        
        return documentRepository.save(document);
    }
    
    @Transactional
    public void incrementAccessCount(Long id) {
        Document document = getDocumentById(id);
        document.setAccessCount(document.getAccessCount() + 1);
        document.setLastAccessedAt(LocalDateTime.now());
        documentRepository.save(document);
    }
    
    @Transactional(readOnly = true)
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Document> searchDocuments(String search, Pageable pageable) {
        return documentRepository.searchDocuments(search, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Document> getDocumentsByCategory(String category) {
        return documentRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Document> getTemplates() {
        return documentRepository.findByIsTemplateTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Document> getDocumentsByEntity(String entityType, Long entityId) {
        return documentRepository.findByRelatedEntityTypeAndRelatedEntityId(entityType, entityId);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return documentRepository.findAllCategories();
    }
    
    @Transactional
    public void deleteDocument(Long id) throws IOException {
        Document document = getDocumentById(id);
        
        // Delete physical file
        Path filePath = Paths.get(uploadDir, DOCUMENTS_FOLDER, document.getFileName());
        Files.deleteIfExists(filePath);
        
        documentRepository.delete(document);
        log.info("Document deleted: " + document.getTitle());
    }
    
    @Transactional
    public Document createNewVersion(Long id, MultipartFile file, String versionNotes) throws IOException {
        Document oldDocument = getDocumentById(id);
        
        // Save new file
        Path documentsPath = Paths.get(uploadDir, DOCUMENTS_FOLDER);
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = documentsPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create new version document
        Document newVersion = new Document();
        newVersion.setTitle(oldDocument.getTitle());
        newVersion.setDescription(oldDocument.getDescription() + " (Versión " + (oldDocument.getVersion() + 1) + ")");
        newVersion.setCategory(oldDocument.getCategory());
        newVersion.setDocumentType(getDocumentType(originalFileName));
        newVersion.setFileName(fileName);
        newVersion.setOriginalFileName(originalFileName);
        newVersion.setFilePath("/uploads/documents/" + fileName);
        newVersion.setFileSize(file.getSize());
        newVersion.setMimeType(file.getContentType());
        newVersion.setTags(oldDocument.getTags());
        newVersion.setUploadedBy(oldDocument.getUploadedBy());
        newVersion.setIsTemplate(oldDocument.getIsTemplate());
        newVersion.setVersion(oldDocument.getVersion() + 1);
        newVersion.setRelatedEntityType(oldDocument.getRelatedEntityType());
        newVersion.setRelatedEntityId(oldDocument.getRelatedEntityId());
        
        return documentRepository.save(newVersion);
    }
    
    private String getDocumentType(String fileName) {
        if (fileName == null) return "OTHER";
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        switch (ext) {
            case "pdf": return "PDF";
            case "doc":
            case "docx": return "WORD";
            case "xls":
            case "xlsx": return "EXCEL";
            case "ppt":
            case "pptx": return "POWERPOINT";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
            case "bmp": return "IMAGE";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
            case "flv":
            case "mkv":
            case "webm": return "VIDEO";
            default: return "OTHER";
        }
    }
    
    public Path getDocumentPath(Document document) {
        return Paths.get(uploadDir, DOCUMENTS_FOLDER, document.getFileName());
    }
}