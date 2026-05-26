package com.cardealer.iotproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    
    // URL base de la API (backend) 34.26.49.182
    @Value("${api.base.url:https://34.26.49.182}")
    private String apiBaseUrl;
    
    // URLs permitidas para CORS (separadas por coma)
    @Value("${cors.allowed.origins:https://34.26.49.182,http://localhost:3000,http://localhost:8085}")
    private String allowedOrigins;
    
    // Host de la base de datos
    @Value("${db.host:localhost}")
    private String dbHost;
    
    @Value("${db.port:3306}")
    private String dbPort;
    
    @Value("${db.name:car_dealer}")
    private String dbName;
    
    // Ruta de uploads
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    // APIs externas
    @Value("${nhtsa.api.url:https://vpic.nhtsa.dot.gov/api}")
    private String nhtsaApiUrl;
    
    // Getters
    public String getApiBaseUrl() { return apiBaseUrl; }
    public String getAllowedOrigins() { return allowedOrigins; }
    public String getDbHost() { return dbHost; }
    public String getDbPort() { return dbPort; }
    public String getDbName() { return dbName; }
    public String getUploadPath() { return uploadPath; }
    public String getNhtsaApiUrl() { return nhtsaApiUrl; }
    
    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }
}