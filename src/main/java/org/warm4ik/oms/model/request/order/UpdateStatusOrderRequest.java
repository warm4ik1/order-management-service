package org.warm4ik.oms.model.request.order;

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
    private OrderStatus status;
}
