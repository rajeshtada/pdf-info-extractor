package com.example.pdfextractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfInfoExtractor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+?\\d{1,3}[-.\\s]?)?(?:\\(?\\d{2,4}\\)?[-.\\s]?)\\d{3}[-.\\s]?\\d{4}");

    public static PdfDetails extractFromPdf(Path pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return extractFromText(text);
        }
    }

    public static PdfDetails extractFromText(String rawText) {
        String text = normalizeText(rawText);

        String email = extractEmail(text);
        String phone = extractPhone(text);
        String name = extractName(text);
        String company = extractCompany(text);
        String address = extractAddress(text);

        return new PdfDetails(name, email, phone, address, company);
    }

    private static String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\t", " ")
                .replace("\u00a0", " ")
                .replaceAll("\\s+", " ");
    }

    private static String extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "Not found";
    }

    private static String extractPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        List<String> candidates = new ArrayList<>();

        while (matcher.find()) {
            String candidate = matcher.group().replaceAll("[^\\d+]", "");
            if (candidate.length() >= 10 && candidate.length() <= 15) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty()) {
            return "Not found";
        }

        return candidates.get(0);
    }

    private static String extractName(String text) {
        List<String> labels = List.of("Name", "Full Name", "Customer Name", "Contact Name", "Applicant Name");
        for (String label : labels) {
            Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(label) + "\\s*[:\-]?\\s*([A-Z][A-Za-z' .-]{2,50})");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String candidate = matcher.group(1).trim();
                if (!candidate.isEmpty() && !candidate.equalsIgnoreCase("N/A")) {
                    return candidate;
                }
            }
        }

        String[] lines = text.split("\\s{2,}");
        for (String line : lines) {
            String clean = line.trim();
            if (clean.isEmpty()) {
                continue;
            }

            if (clean.matches("[A-Z][A-Za-z]+(?: [A-Z][A-Za-z]+){1,3}")) {
                return clean;
            }
        }

        return "Not found";
    }

    private static String extractCompany(String text) {
        List<String> labels = List.of("Company", "Organization", "Employer", "Business Name", "Firm");
        for (String label : labels) {
            Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(label) + "\\s*[:\-]?\\s*([A-Za-z0-9&.,' -]{2,80})");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String candidate = matcher.group(1).trim();
                if (!candidate.isEmpty() && !candidate.equalsIgnoreCase("N/A")) {
                    return candidate;
                }
            }
        }

        for (String keyword : Arrays.asList("Inc", "LLC", "Ltd", "Pvt", "Private Limited", "Corporation", "Technologies")) {
            Pattern pattern = Pattern.compile("(?i)([A-Z][A-Za-z0-9&.,' -]{2,60}\\s(?:" + Pattern.quote(keyword) + "))");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return "Not found";
    }

    private static String extractAddress(String text) {
        List<String> labels = List.of("Address", "Street Address", "Permanent Address", "Current Address", "Location");
        for (String label : labels) {
            Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(label) + "\\s*[:\-]?\\s*([A-Za-z0-9#.,/()' -]{10,200})");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String candidate = matcher.group(1).trim();
                if (candidate.contains("Street") || candidate.contains("Road") || candidate.contains("Avenue") || candidate.contains("Lane") || candidate.contains("City") || candidate.contains("State") || candidate.contains("Zip") || candidate.contains("Pincode")) {
                    return candidate;
                }
            }
        }

        String[] fragments = text.split("\\s{2,}");
        for (String candidate : fragments) {
            String clean = candidate.trim();
            if (clean.isEmpty()) {
                continue;
            }

            if (clean.matches(".*(Street|Road|Avenue|Lane|Drive|City|State|Zip|Pincode|India).*")) {
                return clean;
            }
        }

        return "Not found";
    }
}
