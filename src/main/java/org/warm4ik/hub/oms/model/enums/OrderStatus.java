package org.warm4ik.hub.oms.model.enums;

import lombok.Getter;

import java.util.Set;

@Getter
public enum OrderStatus {
  CREATED,
  IN_PROGRESS,
  COMPLETED;

  private Set<OrderStatus> allowedTransitions;

  static {
    CREATED.allowedTransitions = Set.of(IN_PROGRESS, COMPLETED);
    IN_PROGRESS.allowedTransitions = Set.of(COMPLETED);
    COMPLETED.allowedTransitions = Set.of();
  }

  OrderStatus(){}

  public boolean canTransitionTo(OrderStatus newStatus) {
    return this == newStatus || allowedTransitions.contains(newStatus);
  }
}
