package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.hub.oms.mapper.UserMapper;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public ApiResponse<UserDTO> register(RegisterUserRequest request) {

    User user = userMapper.createUser(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);

    return ApiResponse.createSuccessful(
        "Регистрация прошла успешно!", userMapper.userToUserDTO(user));
  }
}
