package org.warm4ik.oms.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.warm4ik.oms.mapper.OrderMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.dto.OrderDTO;
import org.warm4ik.oms.model.entity.Order;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.enums.OrderStatus;
import org.warm4ik.oms.model.exception.BusinessConflictException;
import org.warm4ik.oms.model.exception.NotFoundException;
import org.warm4ik.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;
import org.warm4ik.oms.repository.OrderRepository;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.security.utils.SecurityUtils;
import org.warm4ik.oms.service.impl.OrderServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderMapper orderMapper;
  @Mock private UserRepository userRepository;

  @InjectMocks private OrderServiceImpl orderService;

  @Test
  void shouldCreateOrderSuccessfullyWhenUserExists() {

    UUID userId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    CreateOrderRequest request = new CreateOrderRequest();
    User user = new User();
    ReflectionTestUtils.setField(user, "id", userId);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);

    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setId(orderId);

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(orderMapper.createOrder(request, user)).thenReturn(order);
      when(orderRepository.save(order)).thenReturn(order);
      when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

      OmsResponse<OrderDTO> response = orderService.createOrder(request);

      assertNotNull(response);
      assertTrue(response.isSuccess());
      assertEquals(orderId, response.getPayload().getId());

      ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
      verify(orderRepository).save(orderCaptor.capture());
      Order savedOrder = orderCaptor.getValue();
      assertEquals(orderId, savedOrder.getId()); // Проверка переданного объекта

      verify(userRepository).findById(userId);
      verify(orderMapper).createOrder(request, user);
      verify(orderMapper).orderToOrderDTO(order);
    }
  }

  @Test
  void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnCreateOrder() {

    UUID userId = UUID.randomUUID();
    CreateOrderRequest request = new CreateOrderRequest();

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      NotFoundException exception =
          assertThrows(NotFoundException.class, () -> orderService.createOrder(request));

      assertEquals(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId), exception.getMessage());

      verify(orderMapper, never()).createOrder(any(), any());
      verify(orderRepository, never()).save(any());
    }
  }

  @Test
  void shouldDeleteOrderSuccessfullyWhenCurrentUserIsOwner() {

    UUID userId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    User user = new User();
    ReflectionTestUtils.setField(user, "id", userId);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setUser(user);

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);
      utilities.when(SecurityUtils::isAdmin).thenReturn(false);

      when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

      orderService.deleteOrderById(orderId);

      verify(orderRepository).delete(order);
    }
  }

  @Test
  void shouldThrowAccessDeniedExceptionWhenUserIsNotOwnerAndNotAdmin() {

    UUID orderId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UUID currentUserId = UUID.randomUUID();

    User user = new User();
    ReflectionTestUtils.setField(user, "id", ownerId);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setUser(user);

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(currentUserId);
      utilities.when(SecurityUtils::isAdmin).thenReturn(false);

      when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

      assertThrows(AccessDeniedException.class, () -> orderService.deleteOrderById(orderId));

      verify(orderRepository, never()).delete(any());
    }
  }

  @Test
  void shouldReturnAllOrdersWithPagination() {

    Pageable pageable = PageRequest.of(0, 2);

    Order order1 = new Order();
    ReflectionTestUtils.setField(order1, "id", UUID.randomUUID());
    Order order2 = new Order();
    ReflectionTestUtils.setField(order2, "id", UUID.randomUUID());

    List<Order> ordersList = List.of(order1, order2);
    Page<Order> orderPage = new PageImpl<>(ordersList, pageable, 10);

    when(orderRepository.findAll(pageable)).thenReturn(orderPage);
    when(orderMapper.orderToOrderDTO(order1)).thenReturn(new OrderDTO());
    when(orderMapper.orderToOrderDTO(order2)).thenReturn(new OrderDTO());

    OmsResponse<PaginationResponse<OrderDTO>> response = orderService.findAllOrders(pageable);

    assertNotNull(response);
    assertTrue(response.isSuccess());
    assertEquals(2, response.getPayload().getContent().size());
    assertEquals(10, response.getPayload().getPagination().getTotal());
    assertEquals(5, response.getPayload().getPagination().getPages());

    verify(orderRepository).findAll(pageable);
    verify(orderMapper).orderToOrderDTO(order1);
    verify(orderMapper).orderToOrderDTO(order2);
  }

  @Test
  void shouldReturnCurrentUserOrdersWithPagination() {

    UUID userId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 2);

    Order order1 = new Order();
    ReflectionTestUtils.setField(order1, "id", UUID.randomUUID());
    Order order2 = new Order();
    ReflectionTestUtils.setField(order2, "id", UUID.randomUUID());

    List<Order> ordersList = List.of(order1, order2);
    Page<Order> orderPage = new PageImpl<>(ordersList, pageable, 10);

    try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      when(orderRepository.findAllByUserId(userId, pageable)).thenReturn(orderPage);
      when(orderMapper.orderToOrderDTO(order1)).thenReturn(new OrderDTO());
      when(orderMapper.orderToOrderDTO(order2)).thenReturn(new OrderDTO());

      OmsResponse<PaginationResponse<OrderDTO>> response =
          orderService.getCurrentUserOrders(pageable);

      assertNotNull(response);
      assertTrue(response.isSuccess());
      assertEquals(2, response.getPayload().getContent().size());
      assertEquals(10, response.getPayload().getPagination().getTotal());
      assertEquals(5, response.getPayload().getPagination().getPages());

      verify(orderRepository).findAllByUserId(userId, pageable);
      verify(orderMapper).orderToOrderDTO(order1);
      verify(orderMapper).orderToOrderDTO(order2);
    }
  }

  @Test
  void shouldUpdateOrderStatusSuccessfully() {

    UUID orderId = UUID.randomUUID();

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setStatus(OrderStatus.CREATED);

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    OrderDTO orderDTO = new OrderDTO();
    ReflectionTestUtils.setField(orderDTO, "id", orderId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

    OmsResponse<OrderDTO> response = orderService.updateStatusOrderById(orderId, request);

    assertNotNull(response);
    assertTrue(response.isSuccess());
    assertEquals(orderDTO, response.getPayload());

    assertEquals(OrderStatus.IN_PROGRESS, order.getStatus(), "Order status should be updated");

    verify(orderRepository).findById(orderId);
    verify(orderRepository).save(order);
    verify(orderMapper).orderToOrderDTO(order);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenOrderNotFound() {

    UUID orderId = UUID.randomUUID();

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> orderService.updateStatusOrderById(orderId, request));

    assertEquals(ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId), exception.getMessage());

    verify(orderRepository).findById(orderId);
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).orderToOrderDTO(any());
  }

  @Test
  void shouldThrowBusinessConflictExceptionWhenStatusTransitionNotAllowed() {

    UUID orderId = UUID.randomUUID();

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setStatus(OrderStatus.COMPLETED);

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    BusinessConflictException exception =
        assertThrows(
            BusinessConflictException.class,
            () -> orderService.updateStatusOrderById(orderId, request));

    assertTrue(
        exception
            .getMessage()
            .contains(ApiErrorMessage.ORDER_STATUS_UPDATE_NOT_ALLOWED.getMessage()),
        "Order status update not allowed.");

    verify(orderRepository).findById(orderId);
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).orderToOrderDTO(any());
  }
}
