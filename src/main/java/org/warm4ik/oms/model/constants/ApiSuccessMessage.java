package org.warm4ik.oms.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiSuccessMessage {

    ORDER_STATUS_UPDATED("The order status has been successfully updated."),
    ORDER_CREATED("The order with id: %s was successfully created."),
    ALL_ORDERS_FETCHED("All available orders have been successfully received."),
    ALL_USERS_FETCHED("All available users have been successfully received."),
    ORDER_FETCHED("The order was successfully found."),
    USER_ORDERS_FETCHED("The user orders was successfully found."),
    REGISTRATION_COMPLETED("Registration completed successfully."),
    PROFILE_LOADED("User profile fetched successfully."),
    LOGIN_SUCCEEDED("Login successful.");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
