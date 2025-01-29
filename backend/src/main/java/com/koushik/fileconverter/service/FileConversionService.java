package com.koushik.fileconverter.service;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.dto.request.ConversionRequestDTO;
import com.koushik.fileconverter.dto.response.FileResponseDTO;
import com.koushik.fileconverter.dto.response.FileResponseDTO.ProcessingStatus;
import com.koushik.fileconverter.exception.FileProcessingException;
import com.koushik.fileconverter.strategy.converter.ConversionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final List<ConversionStrategy> conversionStrategies;
    private final StorageService storageService;
    private final ProgressTrackingService progressTrackingService;
    private final ValidationService validationService;

    public FileResponseDTO convertFile(ConversionRequestDTO request) {
        String jobId = UUID.randomUUID().toString();
        String sourceFilename = request.getFile().getOriginalFilename();
        FileFormat targetFormat = FileFormat.fromExtension(request.getTargetFormat());

        log.info("Starting file conversion. JobId: {}, Source: {}, Target Format: {}", 
                jobId, sourceFilename, targetFormat);

        try {
            // Validate request
            validationService.validateConversionRequest(request);

            // Store original file
            File sourceFile = storageService.storeOriginalFile(request.getFile());
            FileFormat sourceFormat = FileFormat.fromExtension(sourceFilename);
            
            // Find appropriate converter
            ConversionStrategy converter = findConverter(sourceFormat, targetFormat);

            // Generate output filename
            String targetFilename = generateTargetFilename(sourceFilename, targetFormat);
            File targetFile = storageService.createProcessedFile(targetFilename);

            // Start async conversion
            CompletableFuture.runAsync(() -> {
                try {
                    converter.convert(sourceFile, targetFile, 
                        progress -> progressTrackingService.updateProgress(jobId, progress));
                    
                    log.info("File conversion completed successfully. JobId: {}", jobId);
                } catch (Exception e) {
                    log.error("File conversion failed. JobId: {}", jobId, e);
                    progressTrackingService.markAsFailed(jobId, e.getMessage());
                }
            });

            return FileResponseDTO.builder()
                    .jobId(jobId)
                    .fileName(targetFilename)
                    .fileId(targetFilename)
                    .downloadUrl("/api/v1/download/" + targetFilename)
                    .status(ProcessingStatus.PROCESSING)
                    .build();

        } catch (Exception e) {
            log.error("Error during conversion request processing. JobId: {}", jobId, e);
            throw e;
        }
    }

    private ConversionStrategy findConverter(FileFormat sourceFormat, FileFormat targetFormat) {
        return conversionStrategies.stream()
                .filter(strategy -> strategy.supports(sourceFormat, targetFormat))
                .findFirst()
                .orElseThrow(() -> {
                    String message = String.format("Unsupported conversion: %s to %s", 
                            sourceFormat.getExtension(), targetFormat.getExtension());
                    return new FileProcessingException(message);
                });
    }

    private String generateTargetFilename(String sourceFilename, FileFormat targetFormat) {
        String baseName = sourceFilename.substring(0, sourceFilename.lastIndexOf('.'));
        return baseName + "_converted." + targetFormat.getExtension();
    }
}
