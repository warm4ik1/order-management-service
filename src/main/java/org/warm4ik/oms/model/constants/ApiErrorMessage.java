package org.warm4ik.oms.model.constants;

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
  ORDER_STATUS_UPDATE_NOT_ALLOWED("Order status update not allowed."),

  INVALID_TOKEN_SIGNATURE("Invalid token signature"),
  ERROR_DURING_JWT_PROCESSING("An unexpected error occurred during JWT processing."),
  TOKEN_EXPIRED("Token expired."),
  UNEXPECTED_ERROR_OCCURRED("An unexpected error occurred. Please try again later."),
  INVALID_PRINCIPAL_TYPE("JWT authentication failed: expected CustomUserDetails."),

  ACCESS_FORBIDDEN("Access forbidden."),

  INVALID_ENUM_OR_FIELD_VALUE("Invalid enum or field value."),
  MALFORMED_JSON_REQUEST("Malformed JSON request");

  private final String message;

  public String getMessage(Object... args) {
    return String.format(message, args);
  }
}
