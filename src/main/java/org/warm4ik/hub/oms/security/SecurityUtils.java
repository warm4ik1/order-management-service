package org.warm4ik.hub.oms.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.warm4ik.hub.oms.model.constants.ApiErrorMessage;
import org.warm4ik.hub.oms.model.enums.UserRole;

import java.util.UUID;

public class SecurityUtils {

  public static CustomUserDetails currentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
      throw new AuthenticationServiceException(ApiErrorMessage.INVALID_PRINCIPAL_TYPE.getMessage());
    }

    return principal;
  }

  public static UUID currentUserId() {
    return currentUser().getId();
  }

  public static boolean isAdmin() {
    return currentUser().getRole() == UserRole.ADMIN;
  }
}
