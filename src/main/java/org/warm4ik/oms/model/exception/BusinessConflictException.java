package org.warm4ik.oms.model.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessConflictException extends RuntimeException {
  public BusinessConflictException(String message) {
    super(message);
  }
}
