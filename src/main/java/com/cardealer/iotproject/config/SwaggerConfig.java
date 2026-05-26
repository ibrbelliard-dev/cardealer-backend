package com.cardealer.iotproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Car Dealer IoT Project API")
                .version("1.0.0")
                .description("NHTSA Integrated Car Dealer Management System with IoT Support")
                .contact(new Contact()
                    .name("Car Dealer IoT Team")
                    .email("support@cardealer-iot.com")
                    .url("https://cardealer-iot.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")))
            .servers(List.of(
                new Server().url("http://localhost:8082/api").description("Development Server"),
                new Server().url("https://api.cardealer-iot.com/api").description("Production Server")
            ));
    }
}