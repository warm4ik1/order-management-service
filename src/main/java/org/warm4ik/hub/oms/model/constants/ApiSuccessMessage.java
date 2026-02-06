package org.warm4ik.hub.oms.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiSuccessMessage {

    ORDER_STATUS_UPDATED("The order status has been successfully updated."),
    ORDER_CREATED("The order with id: %s was successfully created."),
    ALL_ORDERS_FETCHED("All available orders have been successfully received."),
    ORDER_FOUND("The order was successfully found."),
    REGISTRATION_COMPLETED("Registration completed successfully.");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
