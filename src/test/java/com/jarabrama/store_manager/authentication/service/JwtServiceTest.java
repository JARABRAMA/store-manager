package com.jarabrama.store_manager.authentication.service;

import com.jarabrama.store_manager.TestUtils;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.domain.model.SystemRole;
import com.jarabrama.store_manager.authentication.service.model.NewJwtTokenRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
  private JwtService jwtService;
  private SecretKey signingKey;

  private static final String TEST_SECRET =
          "astringatleast32byteslongforhs256andothersthingslikenumbers12314";

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
    var bites = Decoders.BASE64.decode(TEST_SECRET);
    signingKey = Keys.hmacShaKeyFor(bites);
  }

  private NewJwtTokenRequest buildRequest() {
    return NewJwtTokenRequest.builder()
            .username("user")
            .tokenType(AuthTokenType.ACCESS)
            .userRole(SystemRole.ADMINISTRATOR)
            .expirationTimeout(Duration.ofMinutes(4))
            .sessionId(UUID.randomUUID())
            .trustedDeviceId(UUID.randomUUID())
            .build();
  }

  @Test
  @DisplayName("generateToken returns a non-null, well-formed JWT")
  void generateToken_returnsNonNullToken() {
    String token = jwtService.generateToken(buildRequest());
    System.out.println("created token: " + token);
    assertThat(token).isNotNull().isNotBlank();
    assertEquals(3, token.split("\\.").length);
  }

  @Test
  @DisplayName("generateToken sets the subject to the request username")
  void generateToken_setsSubjectToUsername() {
    var req = buildRequest();

    Claims claims = parseClaims(jwtService.generateToken(req));

    assertThat(claims.getSubject()).isEqualTo(req.username());
  }

  @Test
  @DisplayName("generateToken embeds userRole, sessionId, trustedDeviceId and tokenType as claims")
  void generateToken_includesExtraClaims() {
    var req = buildRequest();

    Claims claims = parseClaims(jwtService.generateToken(req));

    assertThat(claims.get("userRole", String.class)).isEqualTo(req.userRole().toString());
    assertThat(claims.get("sessionId", String.class)).isEqualTo(req.sessionId().toString());
    assertThat(claims.get("trustedDeviceId", String.class)).isEqualTo(req.trustedDeviceId().toString());
    assertThat(claims.get("tokenType", String.class)).isEqualTo(req.tokenType().toString());
  }

  @Test
  @DisplayName("generateToken sets issuedAt to roughly now")
  void generateToken_setsIssuedAtNearNow() {
    Instant before = Instant.now();
    Claims claims = parseClaims(jwtService.generateToken(buildRequest()));
    Instant after = Instant.now();

    assertThat(claims.getIssuedAt()).isBetween(Date.from(before.minusSeconds(2)), Date.from(after.plusSeconds(2)));
  }


  private Claims parseClaims(String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  @Test
  @DisplayName("generateToken with new token jwt token request null fields")
  void generateToken_withNewToken_null_fields() {
    var request = NewJwtTokenRequest.builder().username("user")
            .userRole(SystemRole.ADMINISTRATOR)
            .tokenType(AuthTokenType.ACCESS)
            .expirationTimeout(Duration.ofMinutes(5))
            .build();

    var before = Instant.now();
    var token = jwtService.generateToken(request);
    var after = Instant.now();

    assertThat(token).isNotNull();
    var claims = parseClaims(token);

    assertEquals(request.username(), claims.get("sub", String.class));
    assertEquals(SystemRole.ADMINISTRATOR.toString(), claims.get("userRole", String.class));
    assertEquals(AuthTokenType.ACCESS.toString(), claims.get("tokenType", String.class));
    assertNull(claims.get("sessionId", String.class));
    assertNull(claims.get("trustedDeviceId", String.class));

    assertTrue(TestUtils.isBetweenToDates(claims.getExpiration().toInstant(),
            before.plus(Duration.ofMinutes(5)).minusSeconds(1),
            after.plus(Duration.ofMinutes(5)).plusSeconds(1)));

  }


  @Test
  void getClaimsFrom_Token() {
    var req = NewJwtTokenRequest.builder()
            .username("user")
            .tokenType(AuthTokenType.ACCESS)
            .userRole(SystemRole.ADMINISTRATOR)
            .expirationTimeout(Duration.ofMinutes(5))
            .build();

    var token =  jwtService.generateToken(req);
    var claims = parseClaims(token);

    assertEquals(req.username(), claims.getSubject());
    assertEquals(SystemRole.ADMINISTRATOR.toString(), claims.get("userRole", String.class));
    assertEquals(AuthTokenType.ACCESS.toString(), claims.get("tokenType", String.class));
    assertNull(claims.get("sessionId", String.class));
    assertNull(claims.get("trustedDeviceId", String.class));

  }



}