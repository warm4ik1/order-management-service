package org.warm4ik.hub.oms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("${end.point.orders}")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderServiceImpl;

  @GetMapping()
  public ResponseEntity<ApiResponse<PaginationResponse<OrderDTO>>> getCurrentUserOrders(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    ApiResponse<PaginationResponse<OrderDTO>> response =
        orderServiceImpl.getCurrentUserOrders(pageable);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<OrderDTO>> updateStatusOrderById(
      @PathVariable(name = "id") UUID orderId,
      @RequestBody @Valid UpdateStatusOrderRequest request) {

    ApiResponse<OrderDTO> response = orderServiceImpl.updateStatusOrderById(orderId, request);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<Void> deleteOrderById(@PathVariable(name = "id") UUID orderId) {

    orderServiceImpl.deleteOrderById(orderId);

    return ResponseEntity.noContent().build();
  }

  @PostMapping()
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
      @RequestBody @Valid CreateOrderRequest request) {

    ApiResponse<OrderDTO> response = orderServiceImpl.createOrder(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<PaginationResponse<OrderDTO>>> findAllOrders(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    ApiResponse<PaginationResponse<OrderDTO>> response = orderServiceImpl.findAllOrders(pageable);

    return ResponseEntity.ok(response);
  }
}
