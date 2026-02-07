package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.hub.oms.mapper.UserMapper;
import org.warm4ik.hub.oms.model.constants.ApiErrorMessage;
import org.warm4ik.hub.oms.model.constants.ApiSuccessMessage;
import org.warm4ik.hub.oms.model.dto.TokenDTO;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.exception.DataExistException;
import org.warm4ik.hub.oms.model.exception.NotFoundException;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.repository.UserRepository;
import org.warm4ik.hub.oms.security.CustomUserDetails;
import org.warm4ik.hub.oms.security.JwtTokenProvider;
import org.warm4ik.hub.oms.security.SecurityUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;
  private final JwtTokenProvider jwt;

  @Transactional
  public ApiResponse<UserDTO> register(RegisterUserRequest request) {

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new DataExistException(
          ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()));
    }

    User user = userMapper.createUser(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
    UserDTO userDTO = userMapper.userToUserDTO(user);

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.REGISTRATION_COMPLETED.getMessage(), userDTO);
  }

  public ApiResponse<TokenDTO> login(String username, String password) {

    Authentication auth =
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    Object principal = auth.getPrincipal();

    if (!(principal instanceof CustomUserDetails userDetails)) {
      throw new AuthenticationServiceException(ApiErrorMessage.INVALID_PRINCIPAL_TYPE.getMessage());
    }

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.LOGIN_SUCCESSFUL.getMessage(),
        new TokenDTO(jwt.generateTokenFromPrincipal(userDetails)));
  }

  @Transactional(readOnly = true)
  public ApiResponse<UserDTO> profile() {

    UUID id = SecurityUtils.currentUserId();
    UserDTO userDTO =
        userRepository
            .findById(id)
            .map(userMapper::userToUserDTO)
            .orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage()));

    return ApiResponse.createSuccessful(ApiSuccessMessage.PROFILE_LOADED.getMessage(), userDTO);
  }
}
