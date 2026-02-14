package org.warm4ik.oms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.oms.mapper.OrderMapper;
import org.warm4ik.oms.mapper.PaginationMapper;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.model.constants.ApiSuccessMessage;
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
import org.warm4ik.oms.service.OrderService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public OmsResponse<PaginationResponse<OrderDTO>> getCurrentUserOrders(Pageable pageable) {

    UUID userId = SecurityUtils.currentUserId();

    Page<OrderDTO> orders =
        orderRepository.findAllByUserId(userId, pageable).map(orderMapper::orderToOrderDTO);

    PaginationResponse<OrderDTO> paginationResponse = PaginationMapper.toPaginationResponse(orders);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.USER_ORDERS_FETCHED.getMessage(), paginationResponse);
  }

  @Transactional
  public OmsResponse<OrderDTO> updateStatusOrderById(
      UUID orderId, UpdateStatusOrderRequest request) {

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId)));

    OrderStatus currentStatus = order.getStatus();
    OrderStatus newStatus = request.getStatus();

    if (!currentStatus.canTransitionTo(newStatus)) {
      String errorMessage =
          String.format(
              "%s Cannot change from '%s' to '%s'",
              ApiErrorMessage.ORDER_STATUS_UPDATE_NOT_ALLOWED.getMessage(),
              currentStatus,
              newStatus);
      throw new BusinessConflictException(errorMessage);
    }

    order.setStatus(request.getStatus());
    orderRepository.save(order);
    OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);

    return OmsResponse.createSuccessful(
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
  public OmsResponse<OrderDTO> createOrder(CreateOrderRequest request) {

    UUID userId = SecurityUtils.currentUserId();

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

    Order createdOrder = orderRepository.save(orderMapper.createOrder(request, user));

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.ORDER_CREATED.getMessage(createdOrder.getId()),
        orderMapper.orderToOrderDTO(createdOrder));
  }

  @Transactional(readOnly = true)
  public OmsResponse<PaginationResponse<OrderDTO>> findAllOrders(Pageable pageable) {

    Page<OrderDTO> orders = orderRepository.findAll(pageable).map(orderMapper::orderToOrderDTO);

    PaginationResponse<OrderDTO> paginationResponse = PaginationMapper.toPaginationResponse(orders);

    return OmsResponse.createSuccessful(
        ApiSuccessMessage.ALL_ORDERS_FETCHED.getMessage(), paginationResponse);
  }
}
