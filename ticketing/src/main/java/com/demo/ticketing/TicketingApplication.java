package com.demo.ticketing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "Ticketing API",version = "2.5.0"),
        servers = {@Server(url = "http://localhost:8080")},
        tags = @Tag(name = "Ticketing",description = "C'est la description de l'API ticketing")
)
@SecurityScheme(name = "BasicAuth",type = SecuritySchemeType.HTTP , scheme = "basic", description = "Securité basic avec username et password")
public class TicketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketingApplication.class, args);
    }

}
