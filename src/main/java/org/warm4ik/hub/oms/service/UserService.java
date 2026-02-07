package org.warm4ik.hub.oms.service;

import org.springframework.data.domain.Pageable;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface UserService {

  ApiResponse<PaginationResponse<UserDTO>> findAllUsers(Pageable pageable);

  void deleteUserById(UUID id);
}
