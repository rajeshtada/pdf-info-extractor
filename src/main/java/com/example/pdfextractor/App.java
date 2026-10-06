package com.example.pdfextractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar <jar-file> <pdf-file-path>");
            System.exit(1);
        }

        Path pdfPath = Path.of(args[0]);

        if (!Files.exists(pdfPath)) {
            System.out.println("File not found: " + pdfPath);
            System.exit(1);
        }

        try {
            PdfDetails details = PdfInfoExtractor.extractFromPdf(pdfPath);
            System.out.println(details.toDisplayString());
        } catch (IOException e) {
            System.err.println("Failed to read PDF: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
