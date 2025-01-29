package com.koushik.fileconverter.strategy.converter;

import com.koushik.fileconverter.constant.FileFormat;
import com.koushik.fileconverter.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Consumer;

@Slf4j
@Component
public class PdfToDocxConverter implements ConversionStrategy {

    @Override
    public boolean supports(FileFormat sourceFormat, FileFormat targetFormat) {
        return sourceFormat == FileFormat.PDF && targetFormat == FileFormat.DOCX;
    }

    @Override
    public void convert(File sourceFile, File targetFile, Consumer<Integer> progressCallback) throws FileProcessingException {
        try (PDDocument pdfDocument = Loader.loadPDF(sourceFile);
             XWPFDocument docxDocument = new XWPFDocument()) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);

            // Report initial progress
            progressCallback.accept(20);

            // Create paragraph in DOCX
            XWPFParagraph paragraph = docxDocument.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);

            // Report progress after text extraction
            progressCallback.accept(60);

            // Save the DOCX document
            try (FileOutputStream out = new FileOutputStream(targetFile)) {
                docxDocument.write(out);
            }

            // Report completion
            progressCallback.accept(100);

        } catch (Exception e) {
            log.error("Failed to convert PDF to DOCX", e);
            throw FileProcessingException.conversionFailed(
                FileFormat.PDF.getExtension(),
                FileFormat.DOCX.getExtension(),
                e.getMessage()
            );
        }
    }
}
