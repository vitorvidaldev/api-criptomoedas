package com.vitorvidal.criptomoedas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CriptomoedasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CriptomoedasApplication.class, args);
    }
}
