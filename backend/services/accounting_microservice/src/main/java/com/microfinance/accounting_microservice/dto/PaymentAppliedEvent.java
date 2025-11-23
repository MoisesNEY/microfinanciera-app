package com.microfinance.accounting_microservice.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
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

    @JsonDeserialize(using = FlexibleIntegerDeserializer.class)
    private Integer cashAccountId;

    @JsonDeserialize(using = FlexibleIntegerDeserializer.class)
    private Integer loanReceivableAccountId;

    @JsonDeserialize(using = FlexibleIntegerDeserializer.class)
    private Integer interestIncomeAccountId;

    @JsonDeserialize(using = FlexibleIntegerDeserializer.class)
    private Integer moratoryIncomeAccountId;

    public static class FlexibleUUIDDeserializer extends JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                String value = p.getValueAsString();
                return value != null && !value.isEmpty() ? UUID.fromString(value) : null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static class FlexibleBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
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

    public static class FlexibleIntegerDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                if (p.getCurrentToken().isNumeric()) {
                    return p.getIntValue();
                } else {
                    String value = p.getValueAsString();
                    return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
                }
            } catch (Exception e) {
                return 0;
            }
            }
        }

        public static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                switch (p.getCurrentToken()) {
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                        long epochMillis = p.getLongValue();
                        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
                    case VALUE_STRING:
                        return parseFromString(p.getValueAsString());
                    case START_ARRAY:
                        JsonNode arrayNode = p.readValueAsTree();
                        if (!arrayNode.isArray() || arrayNode.isEmpty()) {
                            return fallbackToNow();
                        }
                        JsonNode firstElement = arrayNode.get(0);
                        if (firstElement.isNumber()) {
                            return LocalDateTime.ofInstant(Instant.ofEpochMilli(firstElement.longValue()), ZoneOffset.UTC);
                        }
                        if (firstElement.isTextual()) {
                            return parseFromString(firstElement.asText());
                        }
                        return fallbackToNow();
                    case START_OBJECT:
                        JsonNode node = p.readValueAsTree();
                        JsonNode valueNode = node.has("$date") ? node.get("$date") : node.has("date") ? node.get("date") : node;
                        if (valueNode.isNumber()) {
                            return LocalDateTime.ofInstant(Instant.ofEpochMilli(valueNode.longValue()), ZoneOffset.UTC);
                        }
                        if (valueNode.isTextual()) {
                            return parseFromString(valueNode.asText());
                        }
                        return fallbackToNow();
                    default:
                        return fallbackToNow();
                }
            } catch (Exception e) {
                return fallbackToNow();
            }
        }

        private LocalDateTime parseFromString(String value) {
            if (value == null || value.isBlank()) {
                return fallbackToNow();
            }
            try {
                return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime();
            } catch (Exception ignored) {
            }
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ignored) {
            }
            return fallbackToNow();
        }

        private LocalDateTime fallbackToNow() {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
    }
}
