package org.warm4ik.oms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.oms.mapper.UserMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.constants.ApiSuccessMessage;
import org.warm4ik.oms.model.dto.TokenDTO;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.exception.DataExistException;
import org.warm4ik.oms.model.exception.NotFoundException;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.security.model.CustomUserDetails;
import org.warm4ik.oms.security.provider.JwtTokenProvider;
import org.warm4ik.oms.security.utils.SecurityUtils;
import org.warm4ik.oms.service.AuthService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authManager;
  private final JwtTokenProvider jwt;

  @Transactional
  @Override
  public OmsResponse<UserDTO> register(RegisterUserRequest request) {

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new DataExistException(
          ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()));
    }

    User user = userMapper.createUser(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
    UserDTO userDTO = userMapper.userToUserDTO(user);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.REGISTRATION_COMPLETED.getMessage(), userDTO);
  }

  @Override
  public OmsResponse<TokenDTO> login(String username, String password) {

    Authentication auth =
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    Object principal = auth.getPrincipal();
    if (!(principal instanceof CustomUserDetails userDetails)) {
      throw new AuthenticationServiceException(ApiErrorMessage.INVALID_PRINCIPAL_TYPE.getMessage());
    }

    String accessToken = jwt.generateAccessToken(userDetails);
    String refreshToken = jwt.generateRefreshToken(userDetails);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.LOGIN_SUCCEEDED.getMessage(), new TokenDTO(accessToken, refreshToken));
  }

  @Transactional(readOnly = true)
  @Override
  public OmsResponse<UserDTO> profile() {

    UUID id = SecurityUtils.currentUserId();
    UserDTO userDTO =
        userRepository
            .findById(id)
            .map(userMapper::userToUserDTO)
            .orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(id)));

    return OmsResponse.createSuccessful(ApiSuccessMessage.PROFILE_LOADED.getMessage(), userDTO);
  }

  @Transactional(readOnly = true)
  @Override
  public OmsResponse<TokenDTO> refreshToken(String refreshToken) {

    if (!jwt.validateToken(refreshToken) || !jwt.isRefreshToken(refreshToken)) {
      throw new AuthenticationServiceException(ApiErrorMessage.INVALID_REFRESH_TOKEN.getMessage());
    }

    UUID userId = jwt.getUserId(refreshToken);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

    CustomUserDetails userDetails = CustomUserDetails.fromEntity(user);

    String newAccessToken = jwt.generateAccessToken(userDetails);
    String newRefreshToken = jwt.generateRefreshToken(userDetails);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.TOKEN_REFRESHED.getMessage(),
        new TokenDTO(newAccessToken, newRefreshToken));
  }
}
