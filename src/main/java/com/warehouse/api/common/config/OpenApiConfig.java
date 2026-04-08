package com.warehouse.api.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI warehouseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse Management API")
                        .description("REST API for managing warehouse items, variants, and stock")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Warehouse")));
    }
}
