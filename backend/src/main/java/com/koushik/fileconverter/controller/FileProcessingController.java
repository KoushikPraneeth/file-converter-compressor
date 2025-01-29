package com.koushik.fileconverter.controller;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.dto.request.CompressionRequestDTO;
import com.koushik.fileconverter.dto.request.ConversionRequestDTO;
import com.koushik.fileconverter.dto.response.FileResponseDTO;
import com.koushik.fileconverter.dto.response.ProgressResponseDTO;
import com.koushik.fileconverter.exception.StorageException;
import com.koushik.fileconverter.service.FileCompressionService;
import com.koushik.fileconverter.service.FileConversionService;
import com.koushik.fileconverter.service.ProgressTrackingService;
import com.koushik.fileconverter.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;

import static com.koushik.fileconverter.constant.ApiConstants.*;

@Slf4j
@RestController
@RequestMapping(API_BASE_PATH)
@RequiredArgsConstructor
public class FileProcessingController {

    private final FileConversionService conversionService;
    private final FileCompressionService compressionService;
    private final ProgressTrackingService progressTrackingService;
    private final StorageService storageService;

    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> convertFile(
            @RequestParam(FILE_PARAM) MultipartFile file,
            @RequestParam(TARGET_FORMAT_PARAM) String targetFormat) {
        
        ConversionRequestDTO request = ConversionRequestDTO.builder()
                .file(file)
                .targetFormat(targetFormat)
                .build();
        
        FileResponseDTO response = conversionService.convertFile(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/compress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDTO> compressFile(
            @RequestParam(FILE_PARAM) MultipartFile file,
            @RequestParam(value = COMPRESSION_LEVEL_PARAM, defaultValue = "50") Integer compressionLevel) {
        
        CompressionRequestDTO request = CompressionRequestDTO.builder()
                .file(file)
                .compressionLevel(compressionLevel)
                .build();
        
        FileResponseDTO response = compressionService.compressFile(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress/{jobId}")
    public SseEmitter trackProgress(@PathVariable(JOB_ID_PARAM) String jobId) {
        return progressTrackingService.subscribeToProgress(jobId);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(FILE_ID_PARAM) String fileId) {
        try {
            File file = storageService.getProcessedFile(fileId);
            Resource resource = new UrlResource(file.toURI());

            String contentType = determineContentType(file.getName());
            String contentDisposition = String.format("attachment; filename=\"%s\"", fileId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .header(CACHE_CONTROL_HEADER, NO_CACHE_VALUE)
                    .body(resource);

        } catch (StorageException | IOException e) {
            log.error("Error downloading file: {}", fileId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<ProgressResponseDTO> getStatus(@PathVariable(JOB_ID_PARAM) String jobId) {
        ProgressResponseDTO progress = progressTrackingService.getProgress(jobId);
        return progress != null ? 
            ResponseEntity.ok(progress) : 
            ResponseEntity.notFound().build();
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        try {
            FileFormat format = FileFormat.fromExtension(extension);
            return format.getMimeType();
        } catch (IllegalArgumentException e) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
