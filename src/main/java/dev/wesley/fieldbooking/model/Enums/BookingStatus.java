package dev.wesley.fieldbooking.model.Enums;

// src/main/java/com/fieldbooking/domain/booking/BookingStatus.java
public enum BookingStatus {
    HOLD,        // slot travado aguardando pagamento
    REQUESTED,   // (opcional) se preferir pré-hold antes do lock
    CONFIRMED,   // pago
    CANCELLED,
    COMPLETED
}
