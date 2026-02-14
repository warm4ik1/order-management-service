package org.warm4ik.oms.model.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.warm4ik.oms.validation.annotation.PasswordMatches;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class RegisterUserRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 8, max = 100, message = "Username must be between 8 and 100 characters")
  @Schema(
      description = "Unique username (from 8 to 100 symbols)",
      example = "alexandra@mail.ru",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
  @Schema(
      description = "User password (from 5 to 50 symbols)",
      example = "password123",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @NotBlank(message = "Confirm password is required")
  @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
  @Schema(
          description = "Confirm password (from 5 to 50 symbols)",
          example = "password123",
          requiredMode = Schema.RequiredMode.REQUIRED)
  private String confirmPassword;
}
