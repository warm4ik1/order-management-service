package org.warm4ik.oms.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.validation.annotation.PasswordMatches;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, RegisterUserRequest> {

  @Override
  public boolean isValid(RegisterUserRequest request, ConstraintValidatorContext context) {

    if (request.getPassword() == null || request.getConfirmPassword() == null) {
      return false;
    }

    return request.getPassword().equals(request.getConfirmPassword());
  }
}
