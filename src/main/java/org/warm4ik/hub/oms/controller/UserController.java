package org.warm4ik.hub.oms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.service.UserService;

import java.util.UUID;

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

  @GetMapping()
  public ResponseEntity<ApiResponse<PaginationResponse<UserDTO>>> findAllUsers(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    ApiResponse<PaginationResponse<UserDTO>> response = userService.findAllUsers(pageable);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserById(@PathVariable(name = "id") UUID id) {

    userService.deleteUserById(id);

    return ResponseEntity.noContent().build();
  }
}
