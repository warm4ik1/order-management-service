package org.warm4ik.oms.unit.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.warm4ik.oms.model.enums.UserRole;
import org.warm4ik.oms.security.model.CustomUserDetails;
import org.warm4ik.oms.security.provider.JwtTokenProvider;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

  private static final String SECRET =
      "aMpXaslkOcN07s1hjpEFR61xOM4aAlLqFSsavyCKWncok1QMYDyFbJSIN0GaZxgRs3aHCKvyvcAKFO05RoVhYx";
  private static final long EXPIRATION = 60_000;

  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION);
  }

  @Test
  @DisplayName("Должен сгенерировать валидный токен и извлечь из него все claims")
  void shouldGenerateValidTokenAndExtractAllClaims() {

    CustomUserDetails principal = createTestPrincipal();

    String token = jwtTokenProvider.generateTokenFromPrincipal(principal);

    assertTrue(jwtTokenProvider.validateToken(token));
    assertEquals(principal.getId(), jwtTokenProvider.getUserId(token));
    assertEquals(principal.getUsername(), jwtTokenProvider.getUsername(token));
    assertEquals(principal.getRole(), jwtTokenProvider.getRole(token));
  }

  @Test
  @DisplayName("Должен корректно извлекать userId, username и роль из токена")
  void shouldExtractAllClaimsFromToken() {

    UUID userId = UUID.randomUUID();
    String username = "username1234";
    UserRole role = UserRole.ADMIN;

    CustomUserDetails principal = CustomUserDetails.fromJwt(userId, username, role);
    String token = jwtTokenProvider.generateTokenFromPrincipal(principal);

    assertEquals(userId, jwtTokenProvider.getUserId(token));
    assertEquals(username, jwtTokenProvider.getUsername(token));
    assertEquals(role, jwtTokenProvider.getRole(token));
  }

  @Test
  @DisplayName("Должен вернуть false для просроченного токена")
  void shouldReturnFalseForExpiredToken() {

    JwtTokenProvider provider = new JwtTokenProvider(SECRET, 0);

    CustomUserDetails principal = createTestPrincipal();
    String token = provider.generateTokenFromPrincipal(principal);

    assertFalse(provider.validateToken(token));
  }

  @Test
  @DisplayName("Должен работать со всеми ролями пользователей (USER, ADMIN)")
  void shouldWorkWithAllUserRoles() {

    for (UserRole role : UserRole.values()) {
      CustomUserDetails principal =
          CustomUserDetails.fromJwt(UUID.randomUUID(), "username1234", role);

      String token = jwtTokenProvider.generateTokenFromPrincipal(principal);

      assertTrue(jwtTokenProvider.validateToken(token));
      assertEquals(role, jwtTokenProvider.getRole(token));
    }
  }

  /* Метод выполнится 5 раз с разными token.
  Для каждого варианта проверяем, что validateToken возвращает false */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "invalid",
        "header.payload",
        "header.payload.",
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0In0.bad-signature"
      })
  @DisplayName("Должен возвращать false для невалидных форматов токена")
  void shouldReturnFalseForInvalidTokenFormats(String invalidToken) {
    assertFalse(jwtTokenProvider.validateToken(invalidToken));
  }

  private CustomUserDetails createTestPrincipal() {
    return CustomUserDetails.fromJwt(UUID.randomUUID(), "testUser", UserRole.USER);
  }
}
