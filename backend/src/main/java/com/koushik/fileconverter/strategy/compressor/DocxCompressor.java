package com.koushik.fileconverter.strategy.compressor;

import com.koushik.fileconverter.constant.CompressionLevel;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class DocxCompressor implements CompressionStrategy {

    @Override
    public boolean supports(String fileExtension) {
        return fileExtension.equalsIgnoreCase("docx");
    }

    @Override
    public void compress(File sourceFile, File targetFile, CompressionLevel level, Consumer<Integer> progressCallback) 
            throws FileProcessingException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Report start
            progressCallback.accept(20);

            int totalImages = document.getAllPictures().size();
            int processedImages = 0;

            for (XWPFPictureData picture : document.getAllPictures()) {
                if (isImage(picture.getFileName())) {
                    byte[] compressedImage = compressImage(picture.getData(), level.getCompressionRatio());
                    // Update the picture data
                    PackagePart part = picture.getPackagePart();
                    try (OutputStream out = part.getOutputStream()) {
                        out.write(compressedImage);
                    }
                }
                processedImages++;
                progressCallback.accept(20 + (60 * processedImages / totalImages));
            }

            // Save compressed document
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                document.write(fos);
            }

            // Report completion
            progressCallback.accept(100);

        } catch (Exception e) {
            log.error("Failed to compress DOCX", e);
            throw FileProcessingException.compressionFailed(sourceFile.getName(), e.getMessage());
        }
    }

    private boolean isImage(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".jpg") || 
               extension.endsWith(".jpeg") || 
               extension.endsWith(".png");
    }

    private byte[] compressImage(byte[] imageData, float compressionRatio) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (ImageOutputStream ios = new MemoryCacheImageOutputStream(compressed)) {
            writer.setOutput(ios);

            JPEGImageWriteParam params = new JPEGImageWriteParam(null);
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(compressionRatio);

            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }

        return compressed.toByteArray();
    }
}
