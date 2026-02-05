package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.warm4ik.hub.oms.mapper.UserMapper;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public ApiResponse<UserDTO> register(RegisterUserRequest request) {

    User user = userMapper.createUser(request);
    userRepository.save(user);

    return ApiResponse.createSuccessful(
        "Регистрация прошла успешно!", userMapper.userToUserDTO(user));
  }
}
