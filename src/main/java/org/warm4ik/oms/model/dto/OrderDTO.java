package org.warm4ik.oms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.warm4ik.oms.model.enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {

  private UUID id;
  private UUID userId;
  private String description;
  private LocalDateTime createdAt;
  private OrderStatus status;
}
