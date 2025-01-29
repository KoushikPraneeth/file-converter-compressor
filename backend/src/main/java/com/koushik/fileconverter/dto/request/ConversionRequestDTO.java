package com.koushik.fileconverter.dto.request;

import com.koushik.fileconverter.constant.FileFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRequestDTO {
    private MultipartFile file;
    private String targetFormat;

    public FileFormat getTargetFileFormat() {
        return FileFormat.fromExtension(targetFormat);
    }
}
