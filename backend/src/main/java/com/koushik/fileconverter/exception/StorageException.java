package com.koushik.fileconverter.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends FileConverterException {
    public StorageException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public StorageException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

    public StorageException(String message, String details) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, details);
    }

    public static StorageException failedToStore(String fileName, String reason) {
        return new StorageException(
            String.format("Failed to store file: %s", fileName),
            reason
        );
    }

    public static StorageException failedToRead(String fileName, String reason) {
        return new StorageException(
            String.format("Failed to read file: %s", fileName),
            reason
        );
    }

    public static StorageException failedToDelete(String fileName, String reason) {
        return new StorageException(
            String.format("Failed to delete file: %s", fileName),
            reason
        );
    }

    public static StorageException directoryCreationFailed(String path, String reason) {
        return new StorageException(
            String.format("Failed to create directory: %s", path),
            reason
        );
    }

    public static StorageException invalidPath(String path) {
        return new StorageException(
            "Invalid file path detected",
            String.format("Path contains invalid characters or is outside allowed directories: %s", path)
        );
    }
}
