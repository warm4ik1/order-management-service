package org.warm4ik.oms.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.warm4ik.oms.mapper.UserMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.enums.UserRole;
import org.warm4ik.oms.model.exception.NotFoundException;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.service.impl.UserServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

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
  void shouldReturnUsersDTOWhenGetAllUsers() {

    Pageable pageable = PageRequest.of(0, 10);

    Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

    when(userRepository.findAll(pageable)).thenReturn(userPage);
    when(userMapper.userToUserDTO(testUser)).thenReturn(testUserDTO);

    OmsResponse<PaginationResponse<UserDTO>> response = userService.findAllUsers(pageable);

    assertNotNull(response);
    assertTrue(response.isSuccess());

    PaginationResponse<UserDTO> payload = response.getPayload();
    assertNotNull(payload);

    assertEquals(1, payload.getContent().size());
    assertEquals(testUserDTO.getUsername(), payload.getContent().getFirst().getUsername());

    PaginationResponse.Pagination pagination = payload.getPagination();
    assertNotNull(pagination);

    assertEquals(1, pagination.getTotal());
    assertEquals(10, pagination.getLimit());
    assertEquals(1, pagination.getPage()); // pageNumber + 1
    assertEquals(1, pagination.getPages());
  }

  @Test
  void shouldReturnVoidWhenDeleteUserById() {

    when(userRepository.existsById(testUser.getId())).thenReturn(true);

    userService.deleteUserById(testUser.getId());

    verify(userRepository, times(1)).deleteById(testUser.getId());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenDeleteUserById() {

    when(userRepository.existsById(testUser.getId())).thenReturn(false);

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(testUser.getId()));

    assertEquals(
        ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(testUser.getId()), exception.getMessage());

    verify(userRepository, never()).deleteById(testUser.getId());
  }
}
