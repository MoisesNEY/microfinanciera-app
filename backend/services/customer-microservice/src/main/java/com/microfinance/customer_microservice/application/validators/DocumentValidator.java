package com.microfinance.customer_microservice.application.validators;

import com.microfinance.customer_microservice.domain.enums.DocumentType;

public class DocumentValidator {

    public static boolean isValidDocument(DocumentType documentType, String documentNumber) {
        if (documentType == null || documentNumber == null || documentNumber.trim().isEmpty()) {
            return false;
        }

        String cleanedDocument = documentNumber.trim().toUpperCase();

        switch (documentType) {
            case CEDULA:
                // Formato nicaragüense: 001-030589-1000A o 0010305891000A
                return cleanedDocument.matches("^\\d{3}-?\\d{6}-?\\d{4}[A-Z]?$");
                
            case PASAPORTE:
                // Formato internacional: A1234567 o AB123456
                return cleanedDocument.matches("^[A-Z]{1,2}[0-9]{6,7}$");
                
            case RESIDENCIA:
                // Formato: R123456789012
                return cleanedDocument.matches("^[A-Z]?\\d{10,14}$");
                
            default:
                return false;
        }
    }

    public static String formatDocument(DocumentType documentType, String documentNumber) {
        if (!isValidDocument(documentType, documentNumber)) {
            throw new IllegalArgumentException("Número de documento inválido para el tipo: " + documentType);
        }

        String cleaned = documentNumber.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");

        switch (documentType) {
            case CEDULA:
                if (cleaned.length() == 13) { // Con letra final
                    return String.format("%s-%s-%s", 
                        cleaned.substring(0, 3), 
                        cleaned.substring(3, 9), 
                        cleaned.substring(9));
                } else { // Sin letra final (12 dígitos)
                    return String.format("%s-%s-%s", 
                        cleaned.substring(0, 3), 
                        cleaned.substring(3, 9), 
                        cleaned.substring(9));
                }
                
            case PASAPORTE:
                return cleaned; // Mantener formato original
                
            case RESIDENCIA:
                return cleaned; // Mantener formato original
                
            default:
                return cleaned;
        }
    }
}