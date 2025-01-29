package com.koushik.fileconverter.strategy.converter;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

@Slf4j
@Component
public class ImageFormatConverter implements ConversionStrategy {

    @Override
    public boolean supports(FileFormat sourceFormat, FileFormat targetFormat) {
        return (sourceFormat == FileFormat.JPG && targetFormat == FileFormat.PNG) ||
               (sourceFormat == FileFormat.PNG && targetFormat == FileFormat.JPG);
    }

    @Override
    public void convert(File sourceFile, File targetFile, Consumer<Integer> progressCallback) throws FileProcessingException {
        try {
            // Report start of reading
            progressCallback.accept(20);

            // Read image
            BufferedImage image = ImageIO.read(sourceFile);

            // Report progress after reading
            progressCallback.accept(50);

            // Write image in target format
            String targetFormat = targetFile.getName().substring(targetFile.getName().lastIndexOf('.') + 1);
            ImageIO.write(image, targetFormat, targetFile);

            // Report completion
            progressCallback.accept(100);

        } catch (Exception e) {
            log.error("Failed to convert image format", e);
            throw FileProcessingException.conversionFailed(
                sourceFile.getName(),
                targetFile.getName(),
                e.getMessage()
            );
        }
    }
}
