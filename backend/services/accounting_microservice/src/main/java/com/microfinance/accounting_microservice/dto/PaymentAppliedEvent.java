package com.microfinance.accounting_microservice.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAppliedEvent {

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID paymentId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID loanId;

    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal capitalAmount;

    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal interestAmount;

    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal moratoryAmount;

    @JsonDeserialize(using = FlexibleBigDecimalDeserializer.class)
    private BigDecimal totalAmount;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime paymentDate;

    private String description;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID cashAccountId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID loanReceivableAccountId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID interestIncomeAccountId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID moratoryIncomeAccountId;

    // Make deserializers package-private (remove 'public')
    static class FlexibleUUIDDeserializer extends JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                JsonNode node = p.readValueAsTree();
                
                if (node.isNumber()) {
                    long numericValue = node.asLong();
                    System.out.println("DEBUG: Converting numeric value " + numericValue + " to UUID");
                    return numericValueToUUID(numericValue);
                } else if (node.isTextual()) {
                    String value = node.asText();
                    if (value != null && !value.trim().isEmpty()) {
                        if (value.contains("-") && value.length() == 36) {
                            return UUID.fromString(value);
                        } else {
                            try {
                                long numericValue = Long.parseLong(value);
                                System.out.println("DEBUG: Converting string numeric value " + numericValue + " to UUID");
                                return numericValueToUUID(numericValue);
                            } catch (NumberFormatException e) {
                                return UUID.fromString(value);
                            }
                        }
                    }
                }
                System.out.println("DEBUG: Failed to deserialize UUID from: " + node);
                return null;
            } catch (Exception e) {
                System.out.println("DEBUG: Error in FlexibleUUIDDeserializer: " + e.getMessage());
                return null;
            }
        }
        
        private UUID numericValueToUUID(long value) {
            return new UUID(0, value);
        }
    }

    static class FlexibleBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                if (p.getCurrentToken().isNumeric()) {
                    return p.getDecimalValue();
                } else {
                    String value = p.getValueAsString();
                    return value != null && !value.isEmpty() ? new BigDecimal(value) : BigDecimal.ZERO;
                }
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }
    }

    static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        // Umbral para distinguir entre milisegundos y días desde el epoch
        // 1000000000 ms = ~1970-01-12, cualquier valor menor probablemente son días
        private static final long MILLIS_THRESHOLD = 1000000000L;
        
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                switch (p.getCurrentToken()) {
                    case VALUE_NUMBER_INT:
                        return parseFromNumber(p.getLongValue());
                    case VALUE_NUMBER_FLOAT:
                        // Manejar números decimales - si es muy pequeño, probablemente es un error
                        double doubleValue = p.getDoubleValue();
                        if (doubleValue < MILLIS_THRESHOLD && doubleValue >= 0) {
                            // Si es un decimal pequeño, podría ser un error de serialización
                            // Intentar como días desde epoch (truncando el decimal)
                            long days = (long) doubleValue;
                            LocalDate date = LocalDate.ofEpochDay(days);
                            // Usar la fecha recibida pero con la hora actual para registrar el momento del procesamiento
                            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                            return date.atTime(now.toLocalTime());
                        } else if (doubleValue >= MILLIS_THRESHOLD) {
                            // Tratar como milisegundos (puede tener decimales)
                            return LocalDateTime.ofInstant(Instant.ofEpochMilli((long) doubleValue), ZoneOffset.UTC);
                        } else {
                            return fallbackToNow();
                        }
                    case VALUE_STRING:
                        return parseFromString(p.getValueAsString());
                    case START_ARRAY:
                        return parseFromArray(p.readValueAsTree());
                    case START_OBJECT:
                        return parseFromObject(p.readValueAsTree());
                    default:
                        return fallbackToNow();
                }
            } catch (Exception e) {
                return fallbackToNow();
            }
        }

        private LocalDateTime parseFromNumber(long numericValue) {
            // Si el número es muy pequeño, probablemente son días desde el epoch (formato LocalDate)
            // o un error de serialización. Valores menores a 1000000000 (aprox 1970-01-12) 
            // no pueden ser milisegundos válidos para fechas recientes
            if (numericValue < MILLIS_THRESHOLD && numericValue >= 0) {
                // Tratar como días desde el epoch (1970-01-01)
                // Esto maneja el caso cuando LocalDate se serializa como días desde epoch
                // Usar la fecha recibida pero con la hora actual para registrar el momento del procesamiento
                LocalDate date = LocalDate.ofEpochDay(numericValue);
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                return date.atTime(now.toLocalTime());
            } else if (numericValue < 0) {
                // Números negativos no son válidos para fechas, usar fallback
                return fallbackToNow();
            } else {
                // Tratar como milisegundos desde el epoch
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(numericValue), ZoneOffset.UTC);
            }
        }

        private LocalDateTime parseFromArray(JsonNode arrayNode) {
            if (!arrayNode.isArray() || arrayNode.size() < 3) {
                return fallbackToNow();
            }
            
            // Jackson serializa LocalDate como [año, mes, día]
            try {
                int year = arrayNode.get(0).asInt();
                int month = arrayNode.get(1).asInt();
                int day = arrayNode.get(2).asInt();
                LocalDate date = LocalDate.of(year, month, day);
                // Usar la fecha recibida pero con la hora actual para registrar el momento del procesamiento
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                return date.atTime(now.toLocalTime());
            } catch (Exception e) {
                return fallbackToNow();
            }
        }

        private LocalDateTime parseFromObject(JsonNode node) {
            JsonNode valueNode = node.has("$date") ? node.get("$date") : 
                                node.has("date") ? node.get("date") : node;
            
            if (valueNode.isNumber()) {
                return parseFromNumber(valueNode.longValue());
            }
            if (valueNode.isTextual()) {
                return parseFromString(valueNode.asText());
            }
            return fallbackToNow();
        }

        private LocalDateTime parseFromString(String value) {
            if (value == null || value.isBlank()) {
                return fallbackToNow();
            }
            
            // Intentar parsear como ISO_DATE_TIME (con hora)
            try {
                return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime();
            } catch (Exception ignored) {
            }
            
            // Intentar parsear como ISO_LOCAL_DATE_TIME (sin zona horaria)
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ignored) {
            }
            
            // Intentar parsear como LocalDate (solo fecha, sin hora)
            try {
                LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return date.atStartOfDay();
            } catch (Exception ignored) {
            }
            
            return fallbackToNow();
        }

        private LocalDateTime fallbackToNow() {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
    }
}