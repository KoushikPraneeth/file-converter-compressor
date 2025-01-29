package com.koushik.fileconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FileConverterApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileConverterApplication.class, args);
    }
}
