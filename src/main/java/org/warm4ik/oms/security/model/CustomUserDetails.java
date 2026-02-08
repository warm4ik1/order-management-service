package org.warm4ik.oms.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.enums.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final UUID id;
  private final String username;
  private final @Nullable String password;
  private final UserRole role;
  private final List<GrantedAuthority> authorities;

  public static UserDetails fromEntity(User user) {
    return new CustomUserDetails(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getRole(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
  }

  public static CustomUserDetails fromJwt(UUID id, String username, UserRole role) {
    return new CustomUserDetails(
        id, username, null, role, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public @Nullable String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }
}
