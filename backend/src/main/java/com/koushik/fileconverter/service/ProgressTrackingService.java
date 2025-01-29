package com.koushik.fileconverter.service;

import com.koushik.fileconverter.dto.response.ProgressResponseDTO;
import com.koushik.fileconverter.dto.response.ProgressResponseDTO.ErrorDetails;
import com.koushik.fileconverter.dto.response.FileResponseDTO.ProcessingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProgressTrackingService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, ProgressResponseDTO> progressCache = new ConcurrentHashMap<>();

    public SseEmitter subscribeToProgress(String jobId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // No timeout
        emitters.put(jobId, emitter);

        // Send initial progress if available
        ProgressResponseDTO initialProgress = progressCache.get(jobId);
        if (initialProgress != null) {
            try {
                emitter.send(SseEmitter.event().name("progress").data(initialProgress));
            } catch (IOException e) {
                log.error("Failed to send initial progress for jobId: {}", jobId, e);
            }
        }

        emitter.onCompletion(() -> emitters.remove(jobId));
        emitter.onTimeout(() -> emitters.remove(jobId));
        emitter.onError(e -> {
            log.error("Error in SseEmitter for jobId: {}", jobId, e);
            emitters.remove(jobId);
        });

        return emitter;
    }

    public void updateProgress(String jobId, int progress) {
        ProgressResponseDTO progressDTO = ProgressResponseDTO.builder()
                .jobId(jobId)
                .progress(progress)
                .status(ProcessingStatus.PROCESSING)
                .build();
        progressCache.put(jobId, progressDTO);
        sendProgressUpdate(jobId, progressDTO);
    }

    public void markAsFailed(String jobId, String errorMessage) {
        ProgressResponseDTO progressDTO = ProgressResponseDTO.builder()
                .jobId(jobId)
                .progress(100)
                .status(ProcessingStatus.FAILED)
                .error(ErrorDetails.builder()
                        .code("PROCESSING_ERROR")
                        .message("File processing failed")
                        .details(errorMessage)
                        .build())
                .build();
        progressCache.put(jobId, progressDTO);
        sendProgressUpdate(jobId, progressDTO);
    }

    public ProgressResponseDTO getProgress(String jobId) {
        return progressCache.get(jobId);
    }

    private void sendProgressUpdate(String jobId, ProgressResponseDTO progressDTO) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("progress").data(progressDTO));
                if (progressDTO.getStatus() == ProcessingStatus.COMPLETED || progressDTO.getStatus() == ProcessingStatus.FAILED) {
                    emitter.complete();
                    emitters.remove(jobId);
                }
            } catch (IOException e) {
                log.error("Failed to send progress update for jobId: {}", jobId, e);
                emitters.remove(jobId);
            }
        }
    }
}
