package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.hub.oms.mapper.UserMapper;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.exception.NotFoundException;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public ApiResponse<PaginationResponse<UserDTO>> findAllUsers(Pageable pageable) {

    Page<UserDTO> users = userRepository.findAll(pageable).map(userMapper::userToUserDTO);

    PaginationResponse<UserDTO> paginationResponse =
        new PaginationResponse<>(
            users.getContent(),
            new PaginationResponse.Pagination(
                users.getTotalElements(),
                pageable.getPageSize(),
                pageable.getPageNumber() + 1,
                users.getTotalPages()));

    return ApiResponse.createSuccessful(
        "Все доступные пользователи успешно получены.", paginationResponse);
  }

  @Transactional
  public void deleteUserById(UUID id) {

    if (!userRepository.existsById(id)) {
      throw new NotFoundException("User с id " + id + " не найден и не может быть удалён.");
    }

    userRepository.deleteById(id);
  }
}
