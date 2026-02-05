package org.warm4ik.hub.oms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.service.UserService;

@RestController
@RequestMapping("${end.point.users}")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserDTO>> register(
      @RequestBody @Valid RegisterUserRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
  }
}
