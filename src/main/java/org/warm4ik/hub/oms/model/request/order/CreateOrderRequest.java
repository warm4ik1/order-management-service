package org.warm4ik.hub.oms.model.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @NotBlank
    @Size(min = 20, max = 500)
    private String description;

}
