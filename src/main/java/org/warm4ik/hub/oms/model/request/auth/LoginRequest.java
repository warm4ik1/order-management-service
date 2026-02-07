package org.warm4ik.hub.oms.model.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 8, max = 100, message = "Username must be between 8 and 100 characters")
  String username;

  @NotBlank(message = "Password is required")
  @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
  String password;
}
