package com.cardealer.iotproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private AppConfig appConfig;
    
    @Value("${upload.path:uploads}")
    private String uploadPath;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configurando CORS con orígenes: {}", appConfig.getAllowedOrigins());
        registry.addMapping("/**")
            .allowedOrigins(appConfig.getAllowedOrigins().split(","))
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Configurando ResourceHandlers con uploadPath: {}", uploadPath);
        
        String location = "file:" + uploadPath + "/";
        log.info("Ubicación de recursos estáticos: {}", location);
        
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location)
            .setCachePeriod(0);
        
        log.info("ResourceHandler registrado para /uploads/** -> {}", location);
    }
}