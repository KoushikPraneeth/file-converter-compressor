package com.koushik.fileconverter.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.koushik.fileconverter.dto.response.FileResponseDTO.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressResponseDTO {
    private String jobId;
    private int progress;
    private String message;
    private ProcessingStatus status;
    private ErrorDetails error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String message;
        private String details;
    }

    public static ProgressResponseDTO failed(String jobId, String code, String message, String details) {
        return ProgressResponseDTO.builder()
                .jobId(jobId)
                .progress(100)
                .status(ProcessingStatus.FAILED)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }
}
