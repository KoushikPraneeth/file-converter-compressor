package com.koushik.fileconverter.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

@Getter
@RequiredArgsConstructor
public enum FileFormat {
    PDF("pdf", MediaType.APPLICATION_PDF_VALUE),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JPG("jpg", MediaType.IMAGE_JPEG_VALUE),
    PNG("png", MediaType.IMAGE_PNG_VALUE);

    private final String extension;
    private final String mimeType;

    public static FileFormat fromExtension(String extension) {
        for (FileFormat format : values()) {
            if (format.getExtension().equalsIgnoreCase(extension)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported file format: " + extension);
    }
}
