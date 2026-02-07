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

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 500, message = "Description must be between 20 and 500 characters")
    private String description;

}
