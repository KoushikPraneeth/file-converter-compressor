package com.koushik.fileconverter.strategy.compressor;

import com.koushik.fileconverter.constant.CompressionLevel;
import com.koushik.fileconverter.exception.FileProcessingException;

import java.io.File;
import java.util.function.Consumer;

public interface CompressionStrategy {
    /**
     * Check if this strategy supports the given file extension
     * @param fileExtension The file extension (e.g., "pdf", "jpg", "docx")
     * @return true if this strategy can compress files with the given extension
     */
    boolean supports(String fileExtension);

    /**
     * Compress the source file and save the result to the target file
     * @param sourceFile The source file to compress
     * @param targetFile The target file to save the compressed result
     * @param level The compression level to apply
     * @param progressCallback Callback to report compression progress (0-100)
     * @throws FileProcessingException if compression fails
     */
    void compress(File sourceFile, File targetFile, CompressionLevel level, Consumer<Integer> progressCallback) 
            throws FileProcessingException;
}
