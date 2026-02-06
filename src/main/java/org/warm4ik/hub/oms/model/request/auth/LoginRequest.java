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

  @NotBlank
  @Size(min = 8, max = 100)
  String username;

  @NotBlank
  @Size(min = 5, max = 50)
  String password;
}
