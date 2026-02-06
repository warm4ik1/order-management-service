package org.warm4ik.hub.oms.model.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.warm4ik.hub.oms.model.enums.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusOrderRequest {

    @NotNull
    private OrderStatus status;
}
