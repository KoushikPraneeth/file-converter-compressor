package com.koushik.fileconverter.strategy.converter;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.exception.FileProcessingException;

import java.io.File;
import java.util.function.Consumer;

public interface ConversionStrategy {
    /**
     * Checks if this strategy can handle the conversion between the given formats
     *
     * @param sourceFormat The source file format
     * @param targetFormat The target file format
     * @return true if the strategy can handle the conversion, false otherwise
     */
    boolean supports(FileFormat sourceFormat, FileFormat targetFormat);

    /**
     * Converts the source file to the target format
     *
     * @param sourceFile The source file to convert
     * @param targetFile The target file to create
     * @param progressCallback Callback to report conversion progress (0-100)
     * @throws FileProcessingException if the conversion fails
     */
    void convert(File sourceFile, File targetFile, Consumer<Integer> progressCallback) throws FileProcessingException;
}
