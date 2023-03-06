package com.jpenacho.starwarsmoviesrepo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class OpenApiConfiguration {

    @Value("${info.app.description}")
    private String description;

    @Value("${spring.webflux.base-path}")
    private String contextPath;

    @Bean
    public OpenAPI getOpenAPI() {

        Contact contact = new Contact()
                .name("João Penacho")
                .email("jpenacho@gmail.com")
                .url("https://www.linkedin.com/in/jo%C3%A3o-penacho-a78a225a/");

        Info info = new Info()
                .title("A Star Wars movies repo")
                .description(description)
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url(contextPath));
    }
}
