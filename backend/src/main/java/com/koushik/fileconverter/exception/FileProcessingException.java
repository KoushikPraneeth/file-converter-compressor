package com.koushik.fileconverter.exception;

import org.springframework.http.HttpStatus;

public class FileProcessingException extends FileConverterException {
    public FileProcessingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

    public FileProcessingException(String message, String details) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, details);
    }

    public static FileProcessingException conversionFailed(String sourceFormat, String targetFormat, String reason) {
        return new FileProcessingException(
            String.format("Failed to convert file from %s to %s", sourceFormat, targetFormat),
            reason
        );
    }

    public static FileProcessingException compressionFailed(String format, String reason) {
        return new FileProcessingException(
            String.format("Failed to compress %s file", format),
            reason
        );
    }

    public static FileProcessingException unsupportedConversion(String sourceFormat, String targetFormat) {
        return new FileProcessingException(
            String.format("Conversion from %s to %s is not supported", sourceFormat, targetFormat)
        );
    }
}
