package com.microfinance.payment_microservice.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    EFECTIVO,
    TRANSFERENCIA,
    TARJETA;

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
