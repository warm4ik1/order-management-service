package org.warm4ik.oms.service;

import org.springframework.data.domain.Pageable;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface UserService {

  OmsResponse<PaginationResponse<UserDTO>> findAllUsers(Pageable pageable);

  void deleteUserById(UUID id);
}
