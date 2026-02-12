package org.warm4ik.oms.unit.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
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

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderMapper orderMapper;
  @Mock private UserRepository userRepository;

  @InjectMocks private OrderServiceImpl orderService;

  @Test
  @DisplayName("Создание заказа: успешное создание, возврат OrderDTO")
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

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      Mockito.when(orderMapper.createOrder(request, user)).thenReturn(order);
      Mockito.when(orderRepository.save(order)).thenReturn(order);
      Mockito.when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

      OmsResponse<OrderDTO> response = orderService.createOrder(request);

      Assertions.assertNotNull(response);
      Assertions.assertTrue(response.isSuccess());
      Assertions.assertEquals(orderId, response.getPayload().getId());

      ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
      Mockito.verify(orderRepository).save(orderCaptor.capture());
      Order savedOrder = orderCaptor.getValue();
      Assertions.assertEquals(orderId, savedOrder.getId()); // Проверка переданного объекта

      Mockito.verify(userRepository).findById(userId);
      Mockito.verify(orderMapper).createOrder(request, user);
      Mockito.verify(orderMapper).orderToOrderDTO(order);
    }
  }

  @Test
  @DisplayName("Создание заказа: ошибка если пользователь не найден (NotFoundException)")
  void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnCreateOrder() {

    UUID userId = UUID.randomUUID();
    CreateOrderRequest request = new CreateOrderRequest();

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

      NotFoundException exception =
          Assertions.assertThrows(NotFoundException.class, () -> orderService.createOrder(request));

      Assertions.assertEquals(
          ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId), exception.getMessage());

      Mockito.verify(orderMapper, Mockito.never()).createOrder(Mockito.any(), Mockito.any());
      Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    }
  }

  @Test
  @DisplayName("Удаление заказа: успешно когда текущий пользователь - владелец")
  void shouldDeleteOrderSuccessfullyWhenCurrentUserIsOwner() {

    UUID userId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();

    User user = new User();
    ReflectionTestUtils.setField(user, "id", userId);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setUser(user);

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);
      utilities.when(SecurityUtils::isAdmin).thenReturn(false);

      Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

      orderService.deleteOrderById(orderId);

      Mockito.verify(orderRepository).delete(order);
    }
  }

  @Test
  @DisplayName(
      "Удаление заказа: ошибка доступа если не владелец и не админ (AccessDeniedException)")
  void shouldThrowAccessDeniedExceptionWhenUserIsNotOwnerAndNotAdmin() {

    UUID orderId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UUID currentUserId = UUID.randomUUID();

    User user = new User();
    ReflectionTestUtils.setField(user, "id", ownerId);

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setUser(user);

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(currentUserId);
      utilities.when(SecurityUtils::isAdmin).thenReturn(false);

      Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

      Assertions.assertThrows(
          AccessDeniedException.class, () -> orderService.deleteOrderById(orderId));

      Mockito.verify(orderRepository, Mockito.never()).delete(Mockito.any());
    }
  }

  @Test
  @DisplayName("Получение всех заказов: пагинированный список всех заказов")
  void shouldReturnAllOrdersWithPagination() {

    Pageable pageable = PageRequest.of(0, 2);

    Order order1 = new Order();
    ReflectionTestUtils.setField(order1, "id", UUID.randomUUID());
    Order order2 = new Order();
    ReflectionTestUtils.setField(order2, "id", UUID.randomUUID());

    List<Order> ordersList = List.of(order1, order2);
    Page<Order> orderPage = new PageImpl<>(ordersList, pageable, 10);

    Mockito.when(orderRepository.findAll(pageable)).thenReturn(orderPage);
    Mockito.when(orderMapper.orderToOrderDTO(order1)).thenReturn(new OrderDTO());
    Mockito.when(orderMapper.orderToOrderDTO(order2)).thenReturn(new OrderDTO());

    OmsResponse<PaginationResponse<OrderDTO>> response = orderService.findAllOrders(pageable);

    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isSuccess());
    Assertions.assertEquals(2, response.getPayload().getContent().size());
    Assertions.assertEquals(10, response.getPayload().getPagination().getTotal());
    Assertions.assertEquals(5, response.getPayload().getPagination().getPages());

    Mockito.verify(orderRepository).findAll(pageable);
    Mockito.verify(orderMapper).orderToOrderDTO(order1);
    Mockito.verify(orderMapper).orderToOrderDTO(order2);
  }

  @Test
  @DisplayName("Получение заказов текущего пользователя: пагинированный список")
  void shouldReturnCurrentUserOrdersWithPagination() {

    UUID userId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 2);

    Order order1 = new Order();
    ReflectionTestUtils.setField(order1, "id", UUID.randomUUID());
    Order order2 = new Order();
    ReflectionTestUtils.setField(order2, "id", UUID.randomUUID());

    List<Order> ordersList = List.of(order1, order2);
    Page<Order> orderPage = new PageImpl<>(ordersList, pageable, 10);

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::currentUserId).thenReturn(userId);

      Mockito.when(orderRepository.findAllByUserId(userId, pageable)).thenReturn(orderPage);
      Mockito.when(orderMapper.orderToOrderDTO(order1)).thenReturn(new OrderDTO());
      Mockito.when(orderMapper.orderToOrderDTO(order2)).thenReturn(new OrderDTO());

      OmsResponse<PaginationResponse<OrderDTO>> response =
          orderService.getCurrentUserOrders(pageable);

      Assertions.assertNotNull(response);
      Assertions.assertTrue(response.isSuccess());
      Assertions.assertEquals(2, response.getPayload().getContent().size());
      Assertions.assertEquals(10, response.getPayload().getPagination().getTotal());
      Assertions.assertEquals(5, response.getPayload().getPagination().getPages());

      Mockito.verify(orderRepository).findAllByUserId(userId, pageable);
      Mockito.verify(orderMapper).orderToOrderDTO(order1);
      Mockito.verify(orderMapper).orderToOrderDTO(order2);
    }
  }

  @Test
  @DisplayName("Обновление статуса заказа: успешное обновление")
  void shouldUpdateOrderStatusSuccessfully() {

    UUID orderId = UUID.randomUUID();

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setStatus(OrderStatus.CREATED);

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    OrderDTO orderDTO = new OrderDTO();
    ReflectionTestUtils.setField(orderDTO, "id", orderId);

    Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    Mockito.when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

    OmsResponse<OrderDTO> response = orderService.updateStatusOrderById(orderId, request);

    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isSuccess());
    Assertions.assertEquals(orderDTO, response.getPayload());

    Assertions.assertEquals(
        OrderStatus.IN_PROGRESS, order.getStatus(), "Order status should be updated");

    Mockito.verify(orderRepository).findById(orderId);
    Mockito.verify(orderRepository).save(order);
    Mockito.verify(orderMapper).orderToOrderDTO(order);
  }

  @Test
  @DisplayName("Обновление статуса заказа: ошибка если заказ не найден (NotFoundException)")
  void shouldThrowNotFoundExceptionWhenOrderNotFound() {

    UUID orderId = UUID.randomUUID();

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    NotFoundException exception =
        Assertions.assertThrows(
            NotFoundException.class, () -> orderService.updateStatusOrderById(orderId, request));

    Assertions.assertEquals(
        ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId), exception.getMessage());

    Mockito.verify(orderRepository).findById(orderId);
    Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    Mockito.verify(orderMapper, Mockito.never()).orderToOrderDTO(Mockito.any());
  }

  @Test
  @DisplayName(
      "Обновление статуса заказа: ошибка при недопустимом переходе статуса (BusinessConflictException)")
  void shouldThrowBusinessConflictExceptionWhenStatusTransitionNotAllowed() {

    UUID orderId = UUID.randomUUID();

    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", orderId);
    order.setStatus(OrderStatus.COMPLETED);

    UpdateStatusOrderRequest request = new UpdateStatusOrderRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    BusinessConflictException exception =
        Assertions.assertThrows(
            BusinessConflictException.class,
            () -> orderService.updateStatusOrderById(orderId, request));

    Assertions.assertTrue(
        exception
            .getMessage()
            .contains(ApiErrorMessage.ORDER_STATUS_UPDATE_NOT_ALLOWED.getMessage()),
        "Order status update not allowed.");

    Mockito.verify(orderRepository).findById(orderId);
    Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
    Mockito.verify(orderMapper, Mockito.never()).orderToOrderDTO(Mockito.any());
  }
}
