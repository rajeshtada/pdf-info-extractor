# PDF Info Extractor

A simple Java Maven project that reads a PDF file and extracts common information such as:
- Name
- Email
- Phone number
- Address
- Company

## Tech stack
- Java 17
- Maven
- Apache PDFBox

## Run

```bash
mvn clean package
java -jar target/pdf-info-extractor-1.0.0.jar /path/to/file.pdf
```

Example:

```bash
java -jar target/pdf-info-extractor-1.0.0.jar sample.pdf
```

The program prints extracted details in a readable format.

## Notes
This is a practical extraction utility based on PDF text parsing and regex matching. It works best with PDFs that contain selectable text. For scanned PDFs or image-based PDFs, OCR would be required.
