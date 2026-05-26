package com.cardealer.iotproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;


@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.cardealer.iotproject"})
public class IbcardealerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbcardealerApplication.class, args);
    }

    @RestController
    public class StaticResourceController {

        @GetMapping("/uploads/company/{filename:.+}")
        public ResponseEntity<Resource> serveCompanyFile(@PathVariable String filename) {
            try {
                String filePath = "/home/ibrbelliard/uploads/company/" + filename;
                File file = new File(filePath);
                
                if (file.exists() && file.canRead()) {
                    Resource resource = new FileSystemResource(file);
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

        @GetMapping("/uploads/vehicles/{filename:.+}")
        public ResponseEntity<Resource> serveVehicleFile(@PathVariable String filename) {
            try {
                String filePath = "/home/ibrbelliard/uploads/vehicles/" + filename;
                File file = new File(filePath);
                
                if (file.exists() && file.canRead()) {
                    Resource resource = new FileSystemResource(file);
                    String contentType = getContentType(filename);
                    return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
                }
            } catch (Exception e) {}
            return ResponseEntity.notFound().build();
        }

        private String getContentType(String filename) {
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
            if (filename.endsWith(".png")) return "image/png";
            return "application/octet-stream";
        }
    }
			
		@GetMapping("/test")
		public String test() {
			return "Controller is working!";
		}

}