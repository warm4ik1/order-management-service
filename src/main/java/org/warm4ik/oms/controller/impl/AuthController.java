package org.warm4ik.oms.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.oms.controller.AuthApi;
import org.warm4ik.oms.model.dto.TokenDTO;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.request.auth.LoginRequest;
import org.warm4ik.oms.model.request.auth.RefreshTokenRequest;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${end.point.auth}")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping("/register")
  @Override
  public ResponseEntity<OmsResponse<UserDTO>> register(
      @RequestBody @Valid RegisterUserRequest request) {

    OmsResponse<UserDTO> response = authService.register(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  @Override
  public ResponseEntity<OmsResponse<TokenDTO>> login(@RequestBody @Valid LoginRequest request) {

    OmsResponse<TokenDTO> response =
        authService.login(request.getUsername(), request.getPassword());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  @Override
  public ResponseEntity<OmsResponse<TokenDTO>> refresh(
      @RequestBody @Valid RefreshTokenRequest request) {

    OmsResponse<TokenDTO> response = authService.refreshToken(request.refreshToken());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Override
  public ResponseEntity<OmsResponse<UserDTO>> profile() {

    OmsResponse<UserDTO> response = authService.profile();

    return ResponseEntity.ok(response);
  }
}
