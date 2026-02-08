package org.warm4ik.hub.oms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;
import org.warm4ik.hub.oms.model.request.order.UpdateStatusOrderRequest;
import org.warm4ik.hub.oms.model.response.OmsResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface OrderApi {

  @Operation(
      summary = "Get current user's orders",
      description = "Returns paginated orders for the currently authenticated user",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  ResponseEntity<OmsResponse<PaginationResponse<OrderDTO>>> getCurrentUserOrders(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit);

  @Operation(
      summary = "Update status of an order by ID",
      description = "Allows admin to update the status of a specific order",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied"),
    @ApiResponse(responseCode = "404", description = "Order not found")
  })
  ResponseEntity<OmsResponse<OrderDTO>> updateStatusOrderById(
      UUID orderId, @Valid UpdateStatusOrderRequest request);

  @Operation(
      summary = "Delete order by ID",
      description = "Allows user or admin to delete an order by its ID",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied"),
    @ApiResponse(responseCode = "404", description = "Order not found")
  })
  ResponseEntity<Void> deleteOrderById(UUID orderId);

  @Operation(
      summary = "Create a new order",
      description = "Allows user or admin to create a new order",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Order created successfully"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  ResponseEntity<OmsResponse<OrderDTO>> createOrder(@Valid CreateOrderRequest request);

  @Operation(
      summary = "Get all orders",
      description = "Allows admin to retrieve paginated list of all orders",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  ResponseEntity<OmsResponse<PaginationResponse<OrderDTO>>> findAllOrders(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "limit", defaultValue = "10") int limit);
}
