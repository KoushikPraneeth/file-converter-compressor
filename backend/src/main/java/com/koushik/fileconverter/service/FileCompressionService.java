package com.koushik.fileconverter.service;

import com.koushik.fileconverter.constant.CompressionLevel;
import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.dto.request.CompressionRequestDTO;
import com.koushik.fileconverter.dto.response.FileResponseDTO;
import com.koushik.fileconverter.dto.response.FileResponseDTO.ProcessingStatus;
import com.koushik.fileconverter.exception.FileProcessingException;
import com.koushik.fileconverter.strategy.compressor.CompressionStrategy;
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
public class FileCompressionService {

    private final List<CompressionStrategy> compressionStrategies;
    private final StorageService storageService;
    private final ProgressTrackingService progressTrackingService;
    private final ValidationService validationService;

    public FileResponseDTO compressFile(CompressionRequestDTO request) {
        String jobId = UUID.randomUUID().toString();
        String sourceFilename = request.getFile().getOriginalFilename();

        log.info("Starting file compression. JobId: {}, Source: {}, Compression Level: {}", 
                jobId, sourceFilename, request.getCompressionLevel());

        try {
            // Validate request
            validationService.validateCompressionRequest(request);

            // Store original file
            File sourceFile = storageService.storeOriginalFile(request.getFile());
            String extension = FileFormat.fromExtension(sourceFilename).getExtension();

            // Find appropriate compressor
            CompressionStrategy compressor = findCompressor(extension);
            CompressionLevel level = request.getCompressionLevel();

            // Generate output filename
            String targetFilename = generateCompressedFilename(sourceFilename);
            File targetFile = storageService.createProcessedFile(targetFilename);

            // Start async compression
            CompletableFuture.runAsync(() -> {
                try {
                    compressor.compress(sourceFile, targetFile, level,
                        progress -> progressTrackingService.updateProgress(jobId, progress));
                    
                    log.info("File compression completed successfully. JobId: {}", jobId);
                } catch (Exception e) {
                    log.error("File compression failed. JobId: {}", jobId, e);
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
            log.error("Error during compression request processing. JobId: {}", jobId, e);
            throw e;
        }
    }

    private CompressionStrategy findCompressor(String extension) {
        return compressionStrategies.stream()
                .filter(strategy -> strategy.supports(extension))
                .findFirst()
                .orElseThrow(() -> {
                    String message = String.format("Unsupported file format for compression: %s", extension);
                    return new FileProcessingException(message);
                });
    }

    private String generateCompressedFilename(String sourceFilename) {
        String baseName = sourceFilename.substring(0, sourceFilename.lastIndexOf('.'));
        String extension = sourceFilename.substring(sourceFilename.lastIndexOf('.'));
        return baseName + "_compressed" + extension;
    }
}
