package com.koushik.fileconverter.strategy.compressor;

import com.koushik.fileconverter.constant.CompressionLevel;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@Component
public class PdfCompressor implements CompressionStrategy {

    @Override
    public boolean supports(String fileExtension) {
        return fileExtension.equalsIgnoreCase("pdf");
    }

    @Override
    public void compress(File sourceFile, File targetFile, CompressionLevel level, Consumer<Integer> progressCallback) 
            throws FileProcessingException {
        try (PDDocument document = Loader.loadPDF(sourceFile);
             PDDocument compressedDoc = new PDDocument()) {

            // Report start
            progressCallback.accept(20);

            int totalPages = document.getNumberOfPages();
            for (int i = 0; i < totalPages; i++) {
                PDPage page = document.getPage(i);
                BufferedImage image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 72);

                // Create new page
                PDPage newPage = new PDPage(page.getMediaBox());
                compressedDoc.addPage(newPage);

                // Create compressed image
                PDImageXObject pdImage = JPEGFactory.createFromImage(compressedDoc, image, level.getCompressionRatio());

                // Add image to page
                try (PDPageContentStream contentStream = new PDPageContentStream(compressedDoc, newPage)) {
                    contentStream.drawImage(pdImage, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                }

                // Report progress
                progressCallback.accept(20 + (70 * (i + 1) / totalPages));
            }

            // Save compressed document
            compressedDoc.save(targetFile);

            // Report completion
            progressCallback.accept(100);

        } catch (IOException e) {
            log.error("Failed to compress PDF", e);
            throw FileProcessingException.compressionFailed(sourceFile.getName(), e.getMessage());
        }
    }
}
