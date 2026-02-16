package org.warm4ik.oms.security.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.warm4ik.oms.model.constants.ApiErrorMessage;
import org.warm4ik.oms.repository.UserRepository;
import org.warm4ik.oms.security.model.CustomUserDetails;

@RequiredArgsConstructor
@NullMarked
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .map(CustomUserDetails::fromEntity)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_NAME.getMessage()));
  }
}
