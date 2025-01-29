package com.koushik.fileconverter.strategy.compressor;

import com.koushik.fileconverter.constant.CompressionLevel;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.function.Consumer;

@Slf4j
@Component
public class ImageCompressor implements CompressionStrategy {

    @Override
    public boolean supports(String fileExtension) {
        return fileExtension.equalsIgnoreCase("jpg") || 
               fileExtension.equalsIgnoreCase("jpeg") ||
               fileExtension.equalsIgnoreCase("png");
    }

    @Override
    public void compress(File sourceFile, File targetFile, CompressionLevel level, Consumer<Integer> progressCallback) 
            throws FileProcessingException {
        try {
            // Report start
            progressCallback.accept(20);

            // Read image
            BufferedImage image = ImageIO.read(sourceFile);

            // Report progress after reading
            progressCallback.accept(40);

            // Get image writer
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new FileProcessingException("No JPEG image writer found");
            }

            ImageWriter writer = writers.next();
            try (ImageOutputStream output = ImageIO.createImageOutputStream(targetFile)) {
                writer.setOutput(output);

                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(level.getCompressionRatio());

                // Report progress before writing
                progressCallback.accept(60);

                // Write compressed image
                writer.write(null, new IIOImage(image, null, null), param);

                // Report completion
                progressCallback.accept(100);
            } finally {
                writer.dispose();
            }

        } catch (Exception e) {
            log.error("Failed to compress image", e);
            throw FileProcessingException.compressionFailed(sourceFile.getName(), e.getMessage());
        }
    }
}
