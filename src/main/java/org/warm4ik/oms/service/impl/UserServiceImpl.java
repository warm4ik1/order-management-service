package org.warm4ik.oms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.oms.mapper.PaginationMapper;
import org.warm4ik.oms.mapper.UserMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.constants.ApiSuccessMessage;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.exception.NotFoundException;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.service.UserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public OmsResponse<PaginationResponse<UserDTO>> findAllUsers(Pageable pageable) {

    Page<UserDTO> users = userRepository.findAll(pageable).map(userMapper::userToUserDTO);

    PaginationResponse<UserDTO> paginationResponse = PaginationMapper.toPaginationResponse(users);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.ALL_USERS_FETCHED.getMessage(), paginationResponse);
  }

  @Transactional
  public void deleteUserById(UUID id) {

    if (!userRepository.existsById(id)) {
      throw new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id));
    }

    userRepository.deleteById(id);
  }
}
