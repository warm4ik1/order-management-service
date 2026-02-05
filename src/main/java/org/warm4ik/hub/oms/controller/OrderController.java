package org.warm4ik.hub.oms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import org.warm4ik.hub.oms.model.dto.OrderSearchDTO;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;
import org.warm4ik.hub.oms.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("${end.point.orders}")
@RequiredArgsConstructor
public class OrderController {
  /* todo: POST /api/orders – создание заказа (требуется авторизация, USER/ADMIN). // DONE 100% БЕЗ АВТОРИЗАЦИИ
      GET /api/orders – список заказов текущего пользователя. // DONE 50% - получение по id вместе нужного т.к нет auth
      GET /api/orders/all – список всех заказов (только для ADMIN). // DONE 100% - с пагинацией - БЕЗ АВТОРИЗАЦИИ
      PUT /api/orders/{id} – обновление статуса заказа (только ADMIN). // DONE 100% БЕЗ АВТОРИЗАЦИИ
      DELETE /api/orders/{id} – удаление заказа (только владелец или ADMIN). // DONE 100% БЕЗ АВТОРИЗАЦИИ*/

  private final OrderService orderService;

  @GetMapping("/{id}") // DONE
  public ResponseEntity<ApiResponse<OrderDTO>> getById(@PathVariable(name = "id") UUID orderId) {

    ApiResponse<OrderDTO> response = orderService.getOrderById(orderId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}") // DONE
  public ResponseEntity<ApiResponse<OrderDTO>> updateStatusOrderById(
      @PathVariable(name = "id") UUID orderId,
      @RequestBody @Valid UpdateStatusOrderRequest request) {

    ApiResponse<OrderDTO> response = orderService.updateStatusOrderById(orderId, request);
    return response.isSuccess()
        ? ResponseEntity.ok(response)
        : ResponseEntity.unprocessableContent().body(response);
  }

  @DeleteMapping("/{id}") // DONE
  public ResponseEntity<Void> deleteOrderById(@PathVariable(name = "id") UUID orderId) {

    orderService.deleteOrderById(orderId);

    return ResponseEntity.noContent().build();
  }

  @PostMapping() // DONE
  public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
      @RequestBody @Valid CreateOrderRequest request) {

    ApiResponse<OrderDTO> response = orderService.createOrder(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/all") // DONE
  public ResponseEntity<ApiResponse<PaginationResponse<OrderSearchDTO>>> findAllOrders(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page, limit);
    ApiResponse<PaginationResponse<OrderSearchDTO>> response = orderService.findAllOrders(pageable);

    return ResponseEntity.ok(response);
  }
}
