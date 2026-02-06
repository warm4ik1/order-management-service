package org.warm4ik.hub.oms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.warm4ik.hub.oms.mapper.OrderMapper;
import org.warm4ik.hub.oms.model.constants.ApiErrorMessage;
import org.warm4ik.hub.oms.model.constants.ApiSuccessMessage;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.dto.OrderSearchDTO;
import org.warm4ik.hub.oms.model.entity.Order;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.enums.OrderStatus;
import org.warm4ik.hub.oms.model.exception.NotFoundException;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.repository.OrderRepository;
import org.warm4ik.hub.oms.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final UserRepository userRepository;

  public ApiResponse<OrderDTO> getOrderById(UUID orderId) {

    OrderDTO orderDTO =
        orderRepository
            .findById(orderId)
            .map(orderMapper::orderToOrderDTO)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId)));

    return ApiResponse.createSuccessful(ApiSuccessMessage.ORDER_FOUND.getMessage(), orderDTO);
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
      return ApiResponse.createFailed(ApiErrorMessage.ORDER_ALREADY_COMPLETED.getMessage());

    order.setStatus(request.getStatus());
    orderRepository.save(order);
    OrderDTO orderDTO = orderMapper.orderToOrderDTO(order);

    return ApiResponse.createSuccessful(
        ApiSuccessMessage.ORDER_STATUS_UPDATED.getMessage(), orderDTO);
  }

  @Transactional
  public void deleteOrderById(UUID orderId) {

    if (!orderRepository.existsById(orderId)) {
      throw new NotFoundException(ApiErrorMessage.ORDER_NOT_FOUND_BY_ID.getMessage(orderId));
    }
    orderRepository.deleteById(orderId);
  }

  @Transactional
  public ApiResponse<OrderDTO> createOrder(CreateOrderRequest request) {

    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(request.getUserId())));

    Order order = orderMapper.createOrder(request, user);
    return ApiResponse.createSuccessful(
        ApiSuccessMessage.ORDER_CREATED.getMessage(order.getId()),
        orderMapper.orderToOrderDTO(orderRepository.save(order)));
  }

  public ApiResponse<PaginationResponse<OrderSearchDTO>> findAllOrders(Pageable pageable) {

    Page<OrderSearchDTO> orders =
        orderRepository.findAll(pageable).map(orderMapper::orderToOrderSearchDTO);
    PaginationResponse<OrderSearchDTO> paginationResponse =
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
