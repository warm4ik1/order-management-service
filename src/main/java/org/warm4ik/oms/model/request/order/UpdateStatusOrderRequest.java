package org.warm4ik.oms.model.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.warm4ik.oms.model.enums.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusOrderRequest {

  @NotNull(message = "Status is required")
  @Schema(
      description = "OrderStatus: CREATED, IN_PROGRESS, COMPLETED",
      example = "IN_PROGRESS",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private OrderStatus status;
}
