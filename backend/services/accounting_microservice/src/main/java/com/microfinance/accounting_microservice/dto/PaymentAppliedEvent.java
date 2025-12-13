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
import java.time.ZonedDateTime;
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

    @JsonDeserialize(using = FlexibleZonedDateTimeDeserializer.class)
    private ZonedDateTime paymentDate;

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
                                System.out
                                        .println("DEBUG: Converting string numeric value " + numericValue + " to UUID");
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

    static class FlexibleZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
        // Thresholds to distinguish between Epoch Days, Epoch Seconds, and Epoch Millis
        // Days: < 100,000 (valid until year ~2243)
        // Seconds: < 100,000,000,000 (valid until year ~5138)
        // Millis: >= 100,000,000,000
        private static final long DAYS_THRESHOLD = 100_000L;
        private static final long SECONDS_THRESHOLD = 100_000_000_000L;
        private static final java.time.ZoneId ZONE_ID = java.time.ZoneId.of("America/Managua");

        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                switch (p.getCurrentToken()) {
                    case VALUE_NUMBER_INT:
                        return parseFromNumber(p.getLongValue());
                    case VALUE_NUMBER_FLOAT:
                        return parseFromNumber(p.getLongValue()); // Treat float timestamp as long (truncate decimals)
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

        private ZonedDateTime parseFromNumber(long numericValue) {
            if (numericValue < 0) {
                return fallbackToNow();
            }

            if (numericValue < DAYS_THRESHOLD) {
                // Treated as Epoch Days
                return LocalDate.ofEpochDay(numericValue).atStartOfDay(ZONE_ID);
            } else if (numericValue < SECONDS_THRESHOLD) {
                // Treated as Epoch Seconds
                return ZonedDateTime.ofInstant(Instant.ofEpochSecond(numericValue), ZONE_ID);
            } else {
                // Treated as Epoch Millis
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(numericValue), ZONE_ID);
            }
        }

        private ZonedDateTime parseFromArray(JsonNode arrayNode) {
            if (!arrayNode.isArray() || arrayNode.size() < 3) {
                return fallbackToNow();
            }

            // Jackson serializa LocalDate como [año, mes, día]
            try {
                int year = arrayNode.get(0).asInt();
                int month = arrayNode.get(1).asInt();
                int day = arrayNode.get(2).asInt();
                LocalDate date = LocalDate.of(year, month, day);
                return date.atStartOfDay(ZoneOffset.UTC);
            } catch (Exception e) {
                return fallbackToNow();
            }
        }

        private ZonedDateTime parseFromObject(JsonNode node) {
            JsonNode valueNode = node.has("$date") ? node.get("$date") : node.has("date") ? node.get("date") : node;

            if (valueNode.isNumber()) {
                return parseFromNumber(valueNode.longValue());
            }
            if (valueNode.isTextual()) {
                return parseFromString(valueNode.asText());
            }
            return fallbackToNow();
        }

        private ZonedDateTime parseFromString(String value) {
            if (value == null || value.isBlank()) {
                return fallbackToNow();
            }

            // Intentar parsear como ISO_DATE_TIME (con hora y posible offset)
            try {
                return ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            } catch (Exception ignored) {
            }

            // Intentar parsear como ISO_DATE_TIME (sin zona explícita, asumir UTC)
            try {
                return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toZonedDateTime();
            } catch (Exception ignored) {
            }

            // Intentar parsear como ISO_LOCAL_DATE_TIME (sin zona horaria)
            try {
                return java.time.LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .atZone(ZoneOffset.UTC);
            } catch (Exception ignored) {
            }

            // Intentar parsear como LocalDate (solo fecha, sin hora)
            try {
                LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return date.atStartOfDay(ZoneOffset.UTC);
            } catch (Exception ignored) {
            }

            return fallbackToNow();
        }

        private ZonedDateTime fallbackToNow() {
            return ZonedDateTime.now(ZoneOffset.UTC);
        }
    }
}