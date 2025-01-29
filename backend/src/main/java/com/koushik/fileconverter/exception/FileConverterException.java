package com.koushik.fileconverter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileConverterException extends RuntimeException {
    private final HttpStatus status;
    private final String details;

    public FileConverterException(HttpStatus status, String message, String details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    public FileConverterException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.details = cause.getMessage();
    }

    public static FileConverterException badRequest(String message, String details) {
        return new FileConverterException(HttpStatus.BAD_REQUEST, message, details);
    }

    public static FileConverterException serverError(String message, String details) {
        return new FileConverterException(HttpStatus.INTERNAL_SERVER_ERROR, message, details);
    }
}
