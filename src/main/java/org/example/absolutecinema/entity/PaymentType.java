package org.example.absolutecinema.entity;

public enum PaymentType {
    TOP_UP, // Пополнение баланса
    FULL_PAYMENT, // Покупка билета
    FINAL_PAYMENT, // Доплата за билет, который был зарезервирован
    RESERVATION, // Бронь билета
    REFUND // Возврат денег за бронь/покупку билета
}
