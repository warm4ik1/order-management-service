package org.warm4ik.hub.oms.service;

import org.springframework.data.domain.Pageable;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface OrderService {

  ApiResponse<PaginationResponse<OrderDTO>> getCurrentUserOrders(Pageable pageable);

  ApiResponse<OrderDTO> updateStatusOrderById(UUID orderId, UpdateStatusOrderRequest request);

  void deleteOrderById(UUID orderId);

  ApiResponse<OrderDTO> createOrder(CreateOrderRequest request);

  ApiResponse<PaginationResponse<OrderDTO>> findAllOrders(Pageable pageable);
}
