package org.warm4ik.oms.unit.service;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.security.model.CustomUserDetails;
import org.warm4ik.oms.security.provider.JwtTokenProvider;
import org.warm4ik.oms.security.utils.SecurityUtils;
import org.warm4ik.oms.service.impl.AuthServiceImpl;

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
  @DisplayName("Регистрация: успешное создание пользователя, возврат UserDTO")
  void shouldReturnUserDTOWhenRegister() {

    String rawPassword = "password123";
    String encodedPassword = "encodedPassword";

    RegisterUserRequest request = new RegisterUserRequest("newUser", rawPassword, rawPassword);

    User newUser = new User();
    newUser.setUsername(request.getUsername());
    newUser.setPassword(request.getPassword());

    Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
    Mockito.when(userMapper.createUser(request)).thenReturn(newUser);
    Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(newUser);
    Mockito.when(userMapper.userToUserDTO(Mockito.any(User.class))).thenReturn(testUserDTO);

    OmsResponse<UserDTO> response = authService.register(request);

    Assertions.assertEquals(
        testUserDTO.getUsername(),
        response.getPayload().getUsername(),
        "Username in DTO should match");

    Mockito.verify(passwordEncoder, Mockito.times(1)).encode(rawPassword);
    Mockito.verify(userMapper, Mockito.times(1)).createUser(request);
    Mockito.verify(userMapper, Mockito.times(1)).userToUserDTO(Mockito.any(User.class));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    Assertions.assertEquals(
        encodedPassword, savedUser.getPassword(), "Saved user should have encoded password");
    Assertions.assertEquals(
        request.getUsername(), savedUser.getUsername(), "Saved user should have correct username");
  }

  @Test
  @DisplayName("Регистрация: ошибка если username уже занят (DataExistException)")
  void shouldThrowDataExistExceptionWhenRegister() {

    RegisterUserRequest request = new RegisterUserRequest("newUser", "rawPassword", "rawPassword");

    Mockito.when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

    DataExistException exception =
        Assertions.assertThrows(DataExistException.class, () -> authService.register(request));

    Assertions.assertEquals(
        ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()),
        exception.getMessage());

    Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
    Mockito.verify(userMapper, Mockito.never()).createUser(Mockito.any());
    Mockito.verify(userMapper, Mockito.never()).userToUserDTO(Mockito.any());
    Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
  }

  @Test
  @DisplayName("Профиль: успешное получение данных пользователя по ID из контекста")
  void shouldReturnUserDTOWhenProfileFound() {

    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setUsername("testUser");
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    userDTO.setUsername("testUser");

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {

      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      Mockito.when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

      OmsResponse<UserDTO> response = authService.profile();

      Assertions.assertNotNull(response);
      Assertions.assertTrue(response.isSuccess());
      Assertions.assertEquals(userDTO.getUsername(), response.getPayload().getUsername());
      Assertions.assertEquals(userId, response.getPayload().getId());

      Mockito.verify(userRepository).findById(userId);
      Mockito.verify(userMapper).userToUserDTO(user);
    }
  }

  @Test
  @DisplayName("Профиль: ошибка если пользователь не найден по ID (NotFoundException)")
  void shouldThrowNotFoundExceptionWhenProfileNotFound() {

    UUID userId = UUID.randomUUID();

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

      NotFoundException exception =
          Assertions.assertThrows(NotFoundException.class, () -> authService.profile());

      Assertions.assertEquals(
          ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId), exception.getMessage());

      Mockito.verify(userMapper, Mockito.never()).userToUserDTO(Mockito.any());
    }
  }

  @Test
  @DisplayName("Логин: успешная аутентификация, возврат JWT токена")
  void shouldReturnTokenDTOWhenLoginSuccessful() {

    UUID userId = UUID.randomUUID();
    String username = "testUser";
    String password = "password123";
    UserRole role = UserRole.USER;
    String token = "jwtToken";

    CustomUserDetails userDetails = CustomUserDetails.fromJwt(userId, username, role);

    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getPrincipal()).thenReturn(userDetails);

    Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(auth);
    Mockito.when(jwt.generateTokenFromPrincipal(userDetails)).thenReturn(token);

    OmsResponse<TokenDTO> response = authService.login(username, password);

    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isSuccess());
    Assertions.assertEquals(token, response.getPayload().accessToken());

    ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    Mockito.verify(authManager).authenticate(authCaptor.capture());

    UsernamePasswordAuthenticationToken captured = authCaptor.getValue();
    Assertions.assertEquals(username, captured.getPrincipal());
    Assertions.assertEquals(password, captured.getCredentials());

    Mockito.verify(jwt).generateTokenFromPrincipal(userDetails);
  }

  @Test
  @DisplayName("Логин: ошибка при некорректном типе Principal (AuthenticationServiceException)")
  void shouldThrowAuthenticationServiceExceptionWhenPrincipalInvalid() {

    String username = "testUser";
    String password = "password123";

    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getPrincipal()).thenReturn("NotACustomUserDetails"); // некорректный тип

    Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(auth);

    AuthenticationServiceException exception =
        Assertions.assertThrows(
            AuthenticationServiceException.class, () -> authService.login(username, password));

    Assertions.assertEquals(
        ApiErrorMessage.INVALID_PRINCIPAL_TYPE.getMessage(), exception.getMessage());

    Mockito.verify(jwt, Mockito.never()).generateTokenFromPrincipal(Mockito.any());
  }
}
