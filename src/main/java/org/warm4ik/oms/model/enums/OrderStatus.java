package org.warm4ik.oms.model.enums;

public enum OrderStatus {
  CREATED,
  IN_PROGRESS,
  COMPLETED;

  public boolean canTransitionTo(OrderStatus newStatus) {
    if (newStatus == null) {
      return false;
    }
    if (this == newStatus) {
      return true;
    }

    return switch (this) {
      case CREATED -> newStatus == IN_PROGRESS || newStatus == COMPLETED;

      case IN_PROGRESS -> newStatus == COMPLETED;

      case COMPLETED -> false;
    };
  }
}
