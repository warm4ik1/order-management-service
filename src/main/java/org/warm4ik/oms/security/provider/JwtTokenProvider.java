package org.warm4ik.oms.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.warm4ik.oms.model.enums.UserRole;
import org.warm4ik.oms.security.model.CustomUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

  private static final String USER_ID_CLAIM = "userId";
  private static final String USERNAME_CLAIM = "username";
  private static final String USER_ROLE_CLAIM = "role";

  private final SecretKey secretKey;
  private final Long jwtValidityInMilliseconds;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration:3600000}") long jwtValidityInMilliseconds) {
    this.secretKey = getKey(secret);
    this.jwtValidityInMilliseconds = jwtValidityInMilliseconds;
  }

  public String generateTokenFromPrincipal(@NonNull CustomUserDetails principal) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(USER_ID_CLAIM, principal.getId().toString());
    claims.put(USERNAME_CLAIM, principal.getUsername());
    claims.put(USER_ROLE_CLAIM, principal.getRole().name());

    return createToken(claims, principal.getUsername());
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims =
          Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getAllClaimsFromToken(token).get(USER_ID_CLAIM, String.class));
  }

  public String getUsername(String token) {
    return getAllClaimsFromToken(token).get(USERNAME_CLAIM, String.class);
  }

  public UserRole getRole(String token) {
    return UserRole.valueOf(getAllClaimsFromToken(token).get(USER_ROLE_CLAIM, String.class));
  }

  private Claims getAllClaimsFromToken(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  private SecretKey getKey(String secretKey64) {
    byte[] decode64 = Decoders.BASE64.decode(secretKey64);
    return Keys.hmacShaKeyFor(decode64);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtValidityInMilliseconds))
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();
  }
}
