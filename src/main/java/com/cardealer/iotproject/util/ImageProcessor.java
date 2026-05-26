package com.cardealer.iotproject.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class ImageProcessor {
    
    private static final Logger log = Logger.getLogger(ImageProcessor.class.getName());
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * Validate image file
     */
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warning("File too large: " + file.getSize() + " bytes");
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warning("Invalid content type: " + contentType);
            return false;
        }
        
        // Validate by trying to read the image
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                log.warning("Unable to read image file");
                return false;
            }
            return true;
        } catch (IOException e) {
            log.severe("Error validating image: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate unique filename for image
     */
    public String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * Save uploaded file to disk
     */
    public Path saveFile(MultipartFile file, Path targetPath) throws IOException {
        // Create directories if they don't exist
        if (Files.notExists(targetPath.getParent())) {
            Files.createDirectories(targetPath.getParent());
        }
        
        // Save the file
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("File saved to: " + targetPath);
        
        return targetPath;
    }
    
    /**
     * Create thumbnail image using Thumbnailator
     */
    public Path createThumbnail(Path sourcePath, Path thumbnailPath, int width, int height) throws IOException {
        // Create directories if they don't exist
        if (Files.notExists(thumbnailPath.getParent())) {
            Files.createDirectories(thumbnailPath.getParent());
        }
        
        // Generate thumbnail
        Thumbnails.of(sourcePath.toFile())
            .size(width, height)
            .keepAspectRatio(true)
            .outputQuality(0.8)
            .toFile(thumbnailPath.toFile());
        
        log.info("Thumbnail created at: " + thumbnailPath);
        return thumbnailPath;
    }
    
    /**
     * Create thumbnail without Thumbnailator (using Java built-in ImageIO)
     */
    public Path createThumbnailNative(Path sourcePath, Path thumbnailPath, int targetWidth, int targetHeight) throws IOException {
        if (Files.notExists(thumbnailPath.getParent())) {
            Files.createDirectories(thumbnailPath.getParent());
        }
        
        // Read original image
        BufferedImage originalImage = ImageIO.read(sourcePath.toFile());
        if (originalImage == null) {
            throw new IOException("Unable to read image: " + sourcePath);
        }
        
        // Calculate new dimensions preserving aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);
        
        // Create thumbnail
        BufferedImage thumbnail = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        // Write thumbnail
        String format = getFileExtension(sourcePath.getFileName().toString());
        ImageIO.write(thumbnail, format, thumbnailPath.toFile());
        
        return thumbnailPath;
    }
    
    /**
     * Create multiple thumbnail sizes
     */
    public void createMultipleThumbnails(Path sourcePath, Path basePath, String filename) throws IOException {
        // Small thumbnail (100x100) - for list views
        Path smallThumb = basePath.resolve("thumb_small_" + filename);
        Thumbnails.of(sourcePath.toFile())
            .size(100, 100)
            .keepAspectRatio(true)
            .outputQuality(0.7)
            .toFile(smallThumb.toFile());
        
        // Medium thumbnail (300x200) - for gallery preview
        Path mediumThumb = basePath.resolve("thumb_medium_" + filename);
        Thumbnails.of(sourcePath.toFile())
            .size(300, 200)
            .keepAspectRatio(true)
            .outputQuality(0.8)
            .toFile(mediumThumb.toFile());
        
        // Large thumbnail (600x400) - for detail view
        Path largeThumb = basePath.resolve("thumb_large_" + filename);
        Thumbnails.of(sourcePath.toFile())
            .size(600, 400)
            .keepAspectRatio(true)
            .outputQuality(0.85)
            .toFile(largeThumb.toFile());
        
        log.info("Multiple thumbnails created for: " + filename);
    }
    
    /**
     * Resize image to specific dimensions
     */
    public byte[] resizeImage(byte[] imageBytes, int targetWidth, int targetHeight, String format) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Thumbnails.of(inputStream)
                .size(targetWidth, targetHeight)
                .keepAspectRatio(true)
                .outputFormat(format)
                .outputQuality(0.9)
                .toOutputStream(outputStream);
            
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Compress image to reduce file size
     */
    public byte[] compressImage(byte[] imageBytes, double quality) throws IOException {
        if (quality < 0 || quality > 1) {
            throw new IllegalArgumentException("Quality must be between 0 and 1");
        }
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            BufferedImage image = ImageIO.read(inputStream);
            String format = detectImageFormat(imageBytes);
            
            Thumbnails.of(image)
                .scale(1.0)
                .outputQuality(quality)
                .outputFormat(format)
                .toOutputStream(outputStream);
            
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Detect image format from bytes
     */
    private String detectImageFormat(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return "jpg";
            }
        }
        return "jpg";
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf(".");
        if (lastDot > 0) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * Get image dimensions
     */
    public ImageDimensions getImageDimensions(Path imagePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(imagePath)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return new ImageDimensions(image.getWidth(), image.getHeight());
            }
        }
        return new ImageDimensions(0, 0);
    }
    
    /**
     * Get image dimensions from bytes
     */
    public ImageDimensions getImageDimensions(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return new ImageDimensions(image.getWidth(), image.getHeight());
            }
        }
        return new ImageDimensions(0, 0);
    }
    
    /**
     * Delete image file and its thumbnails
     */
    public void deleteImageWithThumbnails(Path imagePath, Path thumbnailBasePath, String filename) throws IOException {
        // Delete original
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            log.info("Deleted image: " + imagePath);
        }
        
        // Delete thumbnails
        Path smallThumb = thumbnailBasePath.resolve("thumb_small_" + filename);
        Path mediumThumb = thumbnailBasePath.resolve("thumb_medium_" + filename);
        Path largeThumb = thumbnailBasePath.resolve("thumb_large_" + filename);
        
        Files.deleteIfExists(smallThumb);
        Files.deleteIfExists(mediumThumb);
        Files.deleteIfExists(largeThumb);
    }
    
    /**
     * Convert MultipartFile to byte array
     */
    public byte[] convertToByteArray(MultipartFile file) throws IOException {
        return file.getBytes();
    }
    
    /**
     * Create a watermark on image (placeholder)
     */
    public byte[] addWatermark(byte[] imageBytes, String watermarkText) throws IOException {
        // This is a placeholder - you can implement actual watermarking
        return imageBytes;
    }
    
    /**
     * Validate and sanitize filename
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) return null;
        // Remove any path traversal characters
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
    /**
     * Check if image is a JPEG
     */
    public boolean isJpeg(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg"));
    }
    
    /**
     * Check if image is a PNG
     */
    public boolean isPng(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("image/png");
    }
    
    /**
     * Inner class for image dimensions
     */
    public static class ImageDimensions {
        private final int width;
        private final int height;
        
        public ImageDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public String getAspectRatio() {
            if (height == 0) return "0:0";
            int gcd = gcd(width, height);
            return (width / gcd) + ":" + (height / gcd);
        }
        
        private int gcd(int a, int b) {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }
        
        @Override
        public String toString() {
            return width + "x" + height;
        }
    }
}