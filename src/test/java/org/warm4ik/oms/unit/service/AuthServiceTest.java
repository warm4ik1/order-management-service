package org.warm4ik.oms.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.warm4ik.oms.mapper.UserMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.dto.TokenDTO;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.enums.UserRole;
import org.warm4ik.oms.model.exception.DataExistException;
import org.warm4ik.oms.model.exception.NotFoundException;
import org.warm4ik.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.security.model.CustomUserDetails;
import org.warm4ik.oms.security.provider.JwtTokenProvider;
import org.warm4ik.oms.security.utils.SecurityUtils;
import org.warm4ik.oms.service.impl.AuthServiceImpl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationManager authManager;
  @Mock private JwtTokenProvider jwt;

  @InjectMocks private AuthServiceImpl authService;

  private User testUser;
  private UserDTO testUserDTO;

  @BeforeEach
  void setUp() {

    UUID userId = UUID.randomUUID();
    UserRole adminRole = UserRole.ADMIN;

    testUser = new User();
    ReflectionTestUtils.setField(testUser, "id", userId);
    testUser.setUsername("Username1234");
    testUser.setPassword("encodedPassword");
    testUser.setRole(adminRole);

    testUserDTO = new UserDTO();
    ReflectionTestUtils.setField(testUserDTO, "id", userId);
    testUserDTO.setUsername("Username1234");
  }

  @Test
  void shouldReturnUserDTOWhenRegister() {

    String rawPassword = "password123";
    String encodedPassword = "encodedPassword";

    RegisterUserRequest request = new RegisterUserRequest("newUser", rawPassword);

    User newUser = new User();
    newUser.setUsername(request.getUsername());
    newUser.setPassword(request.getPassword());

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    when(userMapper.createUser(request)).thenReturn(newUser);
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(newUser);
    when(userMapper.userToUserDTO(any(User.class))).thenReturn(testUserDTO);

    OmsResponse<UserDTO> response = authService.register(request);

    assertEquals(
        testUserDTO.getUsername(),
        response.getPayload().getUsername(),
        "Username in DTO should match");

    verify(passwordEncoder, times(1)).encode(rawPassword);
    verify(userMapper, times(1)).createUser(request);
    verify(userMapper, times(1)).userToUserDTO(any(User.class));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals(
        encodedPassword, savedUser.getPassword(), "Saved user should have encoded password");
    assertEquals(
        request.getUsername(), savedUser.getUsername(), "Saved user should have correct username");
  }

  @Test
  void shouldThrowDataExistExceptionWhenRegister() {

    RegisterUserRequest request = new RegisterUserRequest("newUser", "rawPassword");

    when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

    DataExistException exception =
        assertThrows(DataExistException.class, () -> authService.register(request));

    assertEquals(
        ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()),
        exception.getMessage());

    verify(passwordEncoder, never()).encode(anyString());
    verify(userMapper, never()).createUser(any());
    verify(userMapper, never()).userToUserDTO(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void shouldReturnUserDTOWhenProfileFound() {

    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setUsername("testUser");
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    userDTO.setUsername("testUser");

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {

      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

      OmsResponse<UserDTO> response = authService.profile();

      assertNotNull(response);
      assertTrue(response.isSuccess());
      assertEquals(userDTO.getUsername(), response.getPayload().getUsername());
      assertEquals(userId, response.getPayload().getId());

      verify(userRepository).findById(userId);
      verify(userMapper).userToUserDTO(user);
    }
  }

  @Test
  void shouldThrowNotFoundExceptionWhenProfileNotFound() {

    UUID userId = UUID.randomUUID();

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      NotFoundException exception =
          assertThrows(NotFoundException.class, () -> authService.profile());

      assertEquals(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId), exception.getMessage());

      verify(userMapper, never()).userToUserDTO(any());
    }
  }

  @Test
  void shouldReturnTokenDTOWhenLoginSuccessful() {

    UUID userId = UUID.randomUUID();
    String username = "testUser";
    String password = "password123";
    UserRole role = UserRole.USER;
    String token = "jwtToken";

    CustomUserDetails userDetails = CustomUserDetails.fromJwt(userId, username, role);

    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(userDetails);

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
    when(jwt.generateTokenFromPrincipal(userDetails)).thenReturn(token);

    OmsResponse<TokenDTO> response = authService.login(username, password);

    assertNotNull(response);
    assertTrue(response.isSuccess());
    assertEquals(token, response.getPayload().accessToken());

    ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(authManager).authenticate(authCaptor.capture());

    UsernamePasswordAuthenticationToken captured = authCaptor.getValue();
    assertEquals(username, captured.getPrincipal());
    assertEquals(password, captured.getCredentials());

    verify(jwt).generateTokenFromPrincipal(userDetails);
  }

  @Test
  void shouldThrowAuthenticationServiceExceptionWhenPrincipalInvalid() {

    String username = "testUser";
    String password = "password123";

    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn("NotACustomUserDetails"); // некорректный тип

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

    AuthenticationServiceException exception =
        assertThrows(
            AuthenticationServiceException.class, () -> authService.login(username, password));

    assertEquals(ApiErrorMessage.INVALID_PRINCIPAL_TYPE.getMessage(), exception.getMessage());

    verify(jwt, never()).generateTokenFromPrincipal(any());
  }
}
