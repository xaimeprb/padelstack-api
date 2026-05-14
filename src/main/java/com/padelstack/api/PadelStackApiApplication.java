package com.padelstack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PadelStackApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PadelStackApiApplication.class, args);
    }
}
