package com.example.pdfextractor;

public record PdfDetails(String name, String email, String phone, String address, String company) {
    public String toDisplayString() {
        return "Name: " + name + System.lineSeparator() +
                "Email: " + email + System.lineSeparator() +
                "Phone: " + phone + System.lineSeparator() +
                "Address: " + address + System.lineSeparator() +
                "Company: " + company;
    }
}
