package com.gustavo.prueba.tecnica.neology.pruebatecnica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Neology Parking Access API")
                        .version("1.0.0")
                        .description(
                                "API REST para la gestión de acceso, estancias y cobro de vehículos en estacionamiento.")
                        .contact(new Contact()
                                .name("Desarrollo por Gustavo Flores")
                                .email("florescadenagustavo@gmail.com")));
    }
}
