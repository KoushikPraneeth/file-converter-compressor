package com.koushik.fileconverter.strategy.converter;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class DocxToPdfConverter implements ConversionStrategy {

    private static final float MARGIN = 50;
    private static final float LINE_SPACING = 15;
    private static final int FONT_SIZE = 12;

    @Override
    public boolean supports(FileFormat sourceFormat, FileFormat targetFormat) {
        return sourceFormat == FileFormat.DOCX && targetFormat == FileFormat.PDF;
    }

    @Override
    public void convert(File sourceFile, File targetFile, Consumer<Integer> progressCallback) throws FileProcessingException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             XWPFDocument document = new XWPFDocument(fis);
             PDDocument pdf = new PDDocument()) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            PDPage page = new PDPage();
            pdf.addPage(page);

            // Report initial progress
            progressCallback.accept(20);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdf, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, FONT_SIZE);
                contentStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);

                float currentY = page.getMediaBox().getHeight() - MARGIN;

                // Report progress at text processing start
                progressCallback.accept(40);

                for (int i = 0; i < paragraphs.size(); i++) {
                    String text = paragraphs.get(i).getText();
                    if (currentY <= MARGIN) {
                        contentStream.endText();
                        page = new PDPage();
                        pdf.addPage(page);
                        contentStream.close();
                        try (PDPageContentStream newStream = new PDPageContentStream(pdf, page)) {
                            newStream.beginText();
                            newStream.setFont(PDType1Font.TIMES_ROMAN, FONT_SIZE);
                            newStream.newLineAtOffset(MARGIN, page.getMediaBox().getHeight() - MARGIN);
                            currentY = page.getMediaBox().getHeight() - MARGIN;
                            newStream.showText(text);
                            newStream.newLineAtOffset(0, -LINE_SPACING);
                            currentY -= LINE_SPACING;
                            newStream.endText();
                        }
                    } else {
                        contentStream.showText(text);
                        contentStream.newLineAtOffset(0, -LINE_SPACING);
                        currentY -= LINE_SPACING;
                    }

                    // Report incremental progress during text processing
                    progressCallback.accept(40 + (50 * i / paragraphs.size()));
                }

                contentStream.endText();
            }

            pdf.save(targetFile);

            // Report completion
            progressCallback.accept(100);

        } catch (Exception e) {
            log.error("Failed to convert DOCX to PDF", e);
            throw FileProcessingException.conversionFailed(
                FileFormat.DOCX.getExtension(),
                FileFormat.PDF.getExtension(),
                e.getMessage()
            );
        }
    }
}
