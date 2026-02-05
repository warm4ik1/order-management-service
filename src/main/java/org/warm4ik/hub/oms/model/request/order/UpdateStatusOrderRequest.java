package org.warm4ik.hub.oms.model.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.warm4ik.hub.oms.model.enums.OrderStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusOrderRequest implements Serializable {

    @NotNull
    private OrderStatus status;
}
