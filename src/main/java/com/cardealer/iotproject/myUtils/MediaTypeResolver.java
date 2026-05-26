package com.cardealer.iotproject.myUtils;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class MediaTypeResolver {
    
    // Media type constants
    public static final int MEDIA_TYPE_IMAGE = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_DOCUMENT = 3;
    public static final int MEDIA_TYPE_ARCHIVE = 4;
    public static final int MEDIA_TYPE_CODE = 5;
    public static final int MEDIA_TYPE_UNKNOWN = 6;
    
    private static final Map<String, Integer> EXTENSION_TO_MEDIA_TYPE = new HashMap<>();
    
    static {
        // Images
        EXTENSION_TO_MEDIA_TYPE.put("jpg", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("jpeg", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("png", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("gif", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("bmp", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("svg", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("webp", MEDIA_TYPE_IMAGE);
        EXTENSION_TO_MEDIA_TYPE.put("ico", MEDIA_TYPE_IMAGE);
        
        // Videos
        EXTENSION_TO_MEDIA_TYPE.put("mp4", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("avi", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("mov", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("wmv", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("flv", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("mkv", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("webm", MEDIA_TYPE_VIDEO);
        EXTENSION_TO_MEDIA_TYPE.put("mpeg", MEDIA_TYPE_VIDEO);
        
        // Audio
        EXTENSION_TO_MEDIA_TYPE.put("mp3", MEDIA_TYPE_AUDIO);
        EXTENSION_TO_MEDIA_TYPE.put("wav", MEDIA_TYPE_AUDIO);
        EXTENSION_TO_MEDIA_TYPE.put("flac", MEDIA_TYPE_AUDIO);
        EXTENSION_TO_MEDIA_TYPE.put("aac", MEDIA_TYPE_AUDIO);
        EXTENSION_TO_MEDIA_TYPE.put("ogg", MEDIA_TYPE_AUDIO);
        EXTENSION_TO_MEDIA_TYPE.put("m4a", MEDIA_TYPE_AUDIO);
        
        // Documents
        EXTENSION_TO_MEDIA_TYPE.put("pdf", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("doc", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("docx", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("txt", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("rtf", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("xls", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("xlsx", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("ppt", MEDIA_TYPE_DOCUMENT);
        EXTENSION_TO_MEDIA_TYPE.put("pptx", MEDIA_TYPE_DOCUMENT);
        
        // Archives
        EXTENSION_TO_MEDIA_TYPE.put("zip", MEDIA_TYPE_ARCHIVE);
        EXTENSION_TO_MEDIA_TYPE.put("rar", MEDIA_TYPE_ARCHIVE);
        EXTENSION_TO_MEDIA_TYPE.put("7z", MEDIA_TYPE_ARCHIVE);
        EXTENSION_TO_MEDIA_TYPE.put("tar", MEDIA_TYPE_ARCHIVE);
        EXTENSION_TO_MEDIA_TYPE.put("gz", MEDIA_TYPE_ARCHIVE);
        
        // Code files
        EXTENSION_TO_MEDIA_TYPE.put("java", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("py", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("js", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("html", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("css", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("json", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("xml", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("c", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("cpp", MEDIA_TYPE_CODE);
        EXTENSION_TO_MEDIA_TYPE.put("php", MEDIA_TYPE_CODE);
    }
    
    /**
     * Returns the media type integer based on the file extension
     * @param filename the name of the file
     * @return media type integer (0-6), returns MEDIA_TYPE_UNKNOWN (6) if extension is not recognized
     */
    public int getMediaType(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return MEDIA_TYPE_UNKNOWN;
        }
        
        // Extract extension (last dot)
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        return EXTENSION_TO_MEDIA_TYPE.getOrDefault(extension, MEDIA_TYPE_UNKNOWN);
    }
}