package com.algomics.torpedo.gaming.engine.divineegypt;

import com.algomics.torpedo.rng.RNG;
import com.algomics.torpedo.rng.fortuna.Fortuna;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(value = "com.algomics.torpedo.gaming",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.algomics.gaming.*.listeners.*")})
public class TestConfig {

    @Bean
    RNG rng() {
        return new Fortuna();
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
