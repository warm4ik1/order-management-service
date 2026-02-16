package org.warm4ik.oms.controller.impl;

import lombok.RequiredArgsConstructor;
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
import org.warm4ik.oms.controller.UserApi;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;
import org.warm4ik.oms.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("${end.point.users}")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  @GetMapping()
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<OmsResponse<PaginationResponse<UserDTO>>> findAllUsers(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    OmsResponse<PaginationResponse<UserDTO>> response = userService.findAllUsers(pageable);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public ResponseEntity<Void> deleteUserById(@PathVariable(name = "id") UUID id) {

    userService.deleteUserById(id);

    return ResponseEntity.noContent().build();
  }
}
