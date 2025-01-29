package com.koushik.fileconverter.dto.request;

import com.koushik.fileconverter.constant.CompressionLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompressionRequestDTO {
    private MultipartFile file;
    private Integer compressionLevel;

    public CompressionLevel getCompressionLevel() {
        return CompressionLevel.fromQuality(this.compressionLevel);
    }
}
