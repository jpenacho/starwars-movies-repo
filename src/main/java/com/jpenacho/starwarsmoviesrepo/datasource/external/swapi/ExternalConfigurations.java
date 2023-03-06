package com.jpenacho.starwarsmoviesrepo.datasource.external.swapi;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalConfigurations {

    @Getter
    @Value("${external.api.swapi.star-war.base-url}")
    String starWarBaseUrl;
}
