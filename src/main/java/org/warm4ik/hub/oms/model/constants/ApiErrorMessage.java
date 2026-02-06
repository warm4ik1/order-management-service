package org.warm4ik.hub.oms.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiErrorMessage {
  USER_NOT_FOUND_BY_ID("User with ID: %s was not found."),
  USER_NOT_FOUND_BY_NAME("User: '%s' was not found."),
  ORDER_NOT_FOUND_BY_ID("Order with ID: %s was not found."),
  USERNAME_ALREADY_EXISTS("Username: %s already exists."),
  ORDER_ALREADY_COMPLETED("The order has already been completed! The status cannot be changed."),

  INVALID_TOKEN_SIGNATURE("Invalid token signature"),
  ERROR_DURING_JWT_PROCESSING("An unexpected error occurred during JWT processing"),
  TOKEN_EXPIRED("Token expired."),
  UNEXPECTED_ERROR_OCCURRED("An unexpected error occurred. Please try again later."),
  INVALID_PRINCIPAL_TYPE("JWT authentication failed: expected CustomUserDetails.");

  private final String message;

  public String getMessage(Object... args) {
    return String.format(message, args);
  }
}
