package com.koushik.fileconverter.service;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.dto.request.CompressionRequestDTO;
import com.koushik.fileconverter.dto.request.ConversionRequestDTO;
import com.koushik.fileconverter.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ValidationService {

    public void validateConversionRequest(ConversionRequestDTO request) {
        MultipartFile file = request.getFile();
        String targetFormat = request.getTargetFormat();

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File is required for conversion.");
        }

        if (!StringUtils.hasText(targetFormat)) {
            throw new FileValidationException("Target format is required for conversion.");
        }

        try {
            FileFormat.fromExtension(targetFormat);
        } catch (IllegalArgumentException e) {
            throw new FileValidationException("Invalid target format: " + targetFormat);
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new FileValidationException("Invalid file name.");
        }

        String sourceExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        try {
            FileFormat.fromExtension(sourceExtension);
        } catch (IllegalArgumentException e) {
            throw new FileValidationException("Invalid source file format: " + sourceExtension);
        }
    }

    public void validateCompressionRequest(CompressionRequestDTO request) {
        MultipartFile file = request.getFile();
        Integer compressionLevel = request.getCompressionLevel();

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File is required for compression.");
        }

        if (compressionLevel == null) {
            throw new FileValidationException("Compression level is required.");
        }

        if (compressionLevel < 0 || compressionLevel > 100) {
            throw new FileValidationException("Compression level must be between 0 and 100.");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new FileValidationException("Invalid file name.");
        }

        String sourceExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        try {
            FileFormat.fromExtension(sourceExtension);
        } catch (IllegalArgumentException e) {
            throw new FileValidationException("Invalid source file format: " + sourceExtension);
        }
    }
}
