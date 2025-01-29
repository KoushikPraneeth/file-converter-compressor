package com.koushik.fileconverter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private String jobId;
    private String fileName;
    private String fileId;
    private String downloadUrl;
    private long fileSize;
    private String format;
    private ProcessingStatus status;

    public enum ProcessingStatus {
        QUEUED,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
