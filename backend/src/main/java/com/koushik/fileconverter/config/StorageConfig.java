package com.koushik.fileconverter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;

@Configuration
public class StorageConfig {

    @Value("${file.upload-dir:${java.io.tmpdir}/file-converter/uploads}")
    private String uploadDir;

    @Value("${file.processed-dir:${java.io.tmpdir}/file-converter/processed}")
    private String processedDir;

    @Bean
    public void createStorageDirectories() {
        createDirectoryIfNotExists(uploadDir);
        createDirectoryIfNotExists(processedDir);
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public String getProcessedDir() {
        return processedDir;
    }
}
