package org.warm4ik.hub.oms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.hub.oms.model.dto.TokenDTO;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.request.auth.LoginRequest;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("${end.point.auth}")
public class AuthController {

  private final AuthService authServiceImpl;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserDTO>> register(
      @RequestBody @Valid RegisterUserRequest request) {

    ApiResponse<UserDTO> response = authServiceImpl.register(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenDTO>> login(@RequestBody @Valid LoginRequest request) {

    ApiResponse<TokenDTO> response =
        authServiceImpl.login(request.getUsername(), request.getPassword());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserDTO>> profile() {

    ApiResponse<UserDTO> response = authServiceImpl.profile();

    return ResponseEntity.ok(response);
  }
}
