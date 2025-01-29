package com.koushik.fileconverter.service;

import com.koushik.fileconverter.config.StorageConfig;
import com.koushik.fileconverter.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageConfig storageConfig;

    public File storeOriginalFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String filename = file.getOriginalFilename();
            Path destinationFile = Path.of(storageConfig.getOriginalsDir(), filename);

            // Prevent directory traversal attack
            if (!destinationFile.getParent().startsWith(Paths.get(storageConfig.getOriginalsDir()))) {
                throw new StorageException("Cannot store file outside of the designated directory.");
            }

            // Copy the file to the destination
            Files.copy(file.getInputStream(), destinationFile);

            log.info("Stored original file: {}", filename);
            return destinationFile.toFile();
        } catch (IOException e) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new StorageException("Failed to store file.", e);
        }
    }

    public File createProcessedFile(String filename) {
        try {
            Path destinationFile = Path.of(storageConfig.getProcessedDir(), filename);

            // Prevent directory traversal attack
            if (!destinationFile.getParent().startsWith(Paths.get(storageConfig.getProcessedDir()))) {
                throw new StorageException("Cannot create file outside of the designated directory.");
            }

            // Create the file
            Files.createFile(destinationFile);

            log.info("Created processed file: {}", filename);
            return destinationFile.toFile();
        } catch (IOException e) {
            log.error("Failed to create file: {}", filename, e);
            throw new StorageException("Failed to create file.", e);
        }
    }

    public File getProcessedFile(String fileId) {
        try {
            Path filePath = Path.of(storageConfig.getProcessedDir(), fileId);
            File file = filePath.toFile();
            if (!file.exists()) {
                throw new StorageException("File not found: " + fileId);
            }
            return file;
        } catch (Exception e) {
            log.error("Failed to get file: {}", fileId, e);
            throw new StorageException("Failed to get file.", e);
        }
    }

    public void cleanupOldFiles() {
        try {
            Instant oneHourAgo = Instant.now().minus(Duration.ofHours(1));
            Files.list(Paths.get(storageConfig.getOriginalsDir()))
                    .filter(path -> isBefore(path, oneHourAgo))
                    .forEach(this::deleteFile);

            Files.list(Paths.get(storageConfig.getProcessedDir()))
                    .filter(path -> isBefore(path, oneHourAgo))
                    .forEach(this::deleteFile);

            log.info("Cleaned up old files");
        } catch (IOException e) {
            log.error("Failed to cleanup old files", e);
        }
    }

    private boolean isBefore(Path path, Instant time) {
        try {
            return Files.getLastModifiedTime(path).toInstant().isBefore(time);
        } catch (IOException e) {
            log.error("Failed to get last modified time for file: {}", path, e);
            return false;
        }
    }

    private void deleteFile(Path path) {
        try {
            FileSystemUtils.deleteRecursively(path);
            log.info("Deleted file: {}", path);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", path, e);
        }
    }
}
