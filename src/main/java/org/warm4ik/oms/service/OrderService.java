package org.warm4ik.oms.service;

import org.springframework.data.domain.Pageable;
import org.warm4ik.oms.model.dto.OrderDTO;
import org.warm4ik.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.oms.model.response.OmsResponse;
import org.warm4ik.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface OrderService {

  OmsResponse<PaginationResponse<OrderDTO>> getCurrentUserOrders(Pageable pageable);

  OmsResponse<OrderDTO> updateStatusOrderById(UUID orderId, UpdateStatusOrderRequest request);

  void deleteOrderById(UUID orderId);

  OmsResponse<OrderDTO> createOrder(CreateOrderRequest request);

  OmsResponse<PaginationResponse<OrderDTO>> findAllOrders(Pageable pageable);
}
