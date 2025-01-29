package com.koushik.fileconverter.exception;

import org.springframework.http.HttpStatus;

public class FileValidationException extends FileConverterException {
    public FileValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public FileValidationException(String message, String details) {
        super(HttpStatus.BAD_REQUEST, message, details);
    }

    public static FileValidationException fileNotFound(String fileName) {
        return new FileValidationException("File not found: " + fileName);
    }

    public static FileValidationException invalidFormat(String format) {
        return new FileValidationException("Invalid or unsupported file format: " + format);
    }

    public static FileValidationException fileTooLarge(long size, long maxSize) {
        return new FileValidationException(
            "File size exceeds maximum limit",
            String.format("File size: %d bytes, Maximum allowed: %d bytes", size, maxSize)
        );
    }
}
