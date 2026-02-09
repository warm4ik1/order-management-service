package org.warm4ik.oms.model.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(
      description = "Description must be between 20 and 500 characters",
      example = "description for new order ()_()",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String description;
}
