package org.warm4ik.hub.oms.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.warm4ik.hub.oms.model.constants.ApiConstants;
import org.warm4ik.hub.oms.model.exception.BusinessConflictException;
import org.warm4ik.hub.oms.model.exception.DataExistException;
import org.warm4ik.hub.oms.model.exception.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class CommonControllerAdvice {

  @ExceptionHandler
  @ResponseBody
  protected ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    logStackTrace(ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler
  @ResponseBody
  protected ResponseEntity<String> handleBusinessConflictException(BusinessConflictException ex) {
    logStackTrace(ex);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseBody
  protected ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    logStackTrace(ex);
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ex.getMessage());
  }

  @ExceptionHandler(DataExistException.class)
  @ResponseBody
  protected ResponseEntity<String> handleDataExistException(DataExistException ex) {
    logStackTrace(ex);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  protected ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    logStackTrace(ex);

    List<String> errors = ex.getBindingResult().getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .toList();

    return ResponseEntity.badRequest().body(errors);
  }


  private void logStackTrace(Exception ex) {
    StringBuilder stackTrace = new StringBuilder();

    stackTrace.append(ApiConstants.ANSI_RED);

    stackTrace.append(ex.getMessage()).append(ApiConstants.BREAK_LINE);

    if (Objects.nonNull(ex.getCause())) {
      stackTrace.append(ex.getCause().getMessage()).append(ApiConstants.BREAK_LINE);
    }

    Arrays.stream(ex.getStackTrace())
        .filter(st -> st.getClassName().startsWith(ApiConstants.TIME_ZONE_PACKAGE_NAME))
        .forEach(
            st ->
                stackTrace
                    .append(st.getClassName())
                    .append(".")
                    .append(st.getMethodName())
                    .append(" (")
                    .append(st.getLineNumber())
                    .append(") "));

    log.error(stackTrace.append(ApiConstants.ANSI_WHITE).toString());
  }
}
