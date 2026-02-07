package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.hub.oms.mapper.OrderMapper;
import org.warm4ik.hub.oms.model.constants.ApiErrorMessage;
import org.warm4ik.hub.oms.model.constants.ApiSuccessMessage;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.entity.Order;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.enums.OrderStatus;
import org.warm4ik.hub.oms.model.exception.BusinessConflictException;
import org.warm4ik.hub.oms.model.exception.NotFoundException;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.repository.OrderRepository;
import org.warm4ik.hub.oms.repository.UserRepository;
import org.warm4ik.hub.oms.security.utils.SecurityUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final UserRepository userRepository;

  public ApiResponse<PaginationResponse<OrderDTO>> getCurrentUserOrders(Pageable pageable) {

    UUID userId = SecurityUtils.currentUserId();

    Page<OrderDTO> orders =
        orderRepository.findAllByUserId(userId, pageable).map(orderMapper::orderToOrderDTO);

    PaginationResponse<OrderDTO> paginationResponse =
        new PaginationResponse<>(
            orders.getContent(),
            new PaginationResponse.Pagination(
                orders.getTotalElements(), // общее кол-во записей
                pageable.getPageSize(), // размер страницы
                pageable.getPageNumber() + 1, // номер текущей страницы
                orders.getTotalPages() // общее кол-во страниц
                ));

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.USER_ORDERS_FETCHED.getMessage(), paginationResponse);
  }

  @Transactional
  public ApiResponse<OrderDTO> updateStatusOrderById(
      UUID orderId, UpdateStatusOrderRequest request) {

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId)));

    if (OrderStatus.COMPLETED.equals(order.getStatus()))
      throw new BusinessConflictException(
          ApiErrorMessage.ORDER_STATUS_UPDATE_NOT_ALLOWED.getMessage());

    order.setStatus(request.getStatus());
    orderRepository.save(order);
    OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.ORDER_STATUS_UPDATED.getMessage(), orderDTO);
  }

  @Transactional
  public void deleteOrderById(UUID orderId) {

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage()));
    UUID userId = SecurityUtils.currentUserId();

    if (!SecurityUtils.isAdmin() && !order.getUser().getId().equals(userId)) {
      throw new AccessDeniedException(ApiErrorMessage.ACCESS_FORBIDDEN.getMessage());
    }

    orderRepository.delete(order);
  }

  @Transactional
  public ApiResponse<OrderDTO> createOrder(CreateOrderRequest request) {

    UUID userId = SecurityUtils.currentUserId();

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

    Order createdOrder = orderRepository.save(orderMapper.createOrder(request, user));

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.ORDER_CREATED.getMessage(createdOrder.getId()),
        orderMapper.orderToOrderDTO(createdOrder));
  }

  public ApiResponse<PaginationResponse<OrderDTO>> findAllOrders(Pageable pageable) {

    Page<OrderDTO> orders = orderRepository.findAll(pageable).map(orderMapper::orderToOrderDTO);
    PaginationResponse<OrderDTO> paginationResponse =
        new PaginationResponse<>(
            orders.getContent(),
            new PaginationResponse.Pagination(
                orders.getTotalElements(), // общее кол-во записей
                pageable.getPageSize(), // размер страницы
                pageable.getPageNumber() + 1, // номер текущей страницы
                orders.getTotalPages() // общее кол-во страниц
                ));

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.ALL_ORDERS_FETCHED.getMessage(), paginationResponse);
  }
}
