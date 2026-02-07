package org.warm4ik.hub.oms.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.warm4ik.hub.oms.model.enums.UserRole;
import org.warm4ik.hub.oms.security.model.CustomUserDetails;
import org.warm4ik.hub.oms.security.provider.JwtTokenProvider;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

  private static final String AUTH_HEADER = "Authorization";
  private static final String BEARER = "Bearer ";

  private final JwtTokenProvider jwt;

  @Override
  @NullMarked
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(AUTH_HEADER);

    if (header != null && header.startsWith(BEARER)) {
      String token = header.substring(BEARER.length());

      try {
        if (jwt.validateToken(token)) {

          UUID userId = jwt.getUserId(token);
          String username = jwt.getUsername(token);
          UserRole role = jwt.getRole(token);

          CustomUserDetails principal = CustomUserDetails.fromJwt(userId, username, role);

          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

          SecurityContextHolder.getContext().setAuthentication(auth);
        }

      } catch (Exception ignored) {
        // просто не авторизуем пользователя
      }
    }
    filterChain.doFilter(request, response);
  }
}
