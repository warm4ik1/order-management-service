package org.warm4ik.hub.oms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("${end.point.users}")
@RequiredArgsConstructor
public class UserController {

  private final UserService userServiceImpl;

  @GetMapping()
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<PaginationResponse<UserDTO>>> findAllUsers(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    ApiResponse<PaginationResponse<UserDTO>> response = userServiceImpl.findAllUsers(pageable);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUserById(@PathVariable(name = "id") UUID id) {

    userServiceImpl.deleteUserById(id);

    return ResponseEntity.noContent().build();
  }
}
