package com.algomics.torpedo.gaming.engine.neon.emulator;

import com.algomics.torpedo.gaming.engine.api.runner.EmulatorCommandLineRunner;
import com.algomics.torpedo.rng.RNG;
import com.algomics.torpedo.rng.fortuna.Fortuna;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Profile("emulator")
@SpringBootApplication(scanBasePackages = "com.algomics.torpedo.gaming.engine")
@Slf4j
public class NeonEmulator extends EmulatorCommandLineRunner implements CommandLineRunner {


    private ApplicationContext ctx;


    public static void main(String[] args) {
        SpringApplication.run(NeonEmulator.class, args);
    }

    @Bean
    @Primary
    RNG rng() {
        return new Fortuna();
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Bean
    Map<String, Object> bursterMeter(){
      return new HashMap<>();
    }

}
