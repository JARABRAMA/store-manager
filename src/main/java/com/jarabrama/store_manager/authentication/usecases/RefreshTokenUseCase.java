package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.AuthException;
import com.jarabrama.store_manager.authentication.domain.model.AuthToken;
import com.jarabrama.store_manager.authentication.domain.model.AuthTokenType;
import com.jarabrama.store_manager.authentication.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use case that issues a new access token from a valid refresh token.
 *
 * <p>Validates the presented refresh token in the following order:
 * <ol>
 *   <li>The token must be of type {@link AuthTokenType#REFRESH}.</li>
 *   <li>The token must be signed with the expected secret key.</li>
 *   <li>The token must not be expired or revoked.</li>
 *   <li>The user the token belongs to must still exist in the system.</li>
 * </ol>
 *
 * <p>When all validations pass, a new access token is generated for the user via
 * {@link CreateNewAccessTokenUseCase}. The refresh token itself is left unchanged;
 * no new refresh token is issued.
 *
 * <p>If any validation fails, an {@link AuthException} is thrown so the caller can
 * respond with HTTP 401 and prompt the user to log in again.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {
  private final JwtService jwtService;
  private final SystemUserRepositoryPort userRepo;
  private final CreateNewAccessTokenUseCase createNewAccessTokenUseCase;

  /**
   * Generates a new access token from the given refresh token.
   *
   * @param refreshToken the refresh token presented by the user.
   * @return the newly created access token for the user.
   * @throws AuthException if the token is not of type {@code REFRESH}, is not signed
   *                       with the expected secret, is expired or revoked, or the
   *                       user it belongs to no longer exists.
   */
  public AuthToken execute(AuthToken refreshToken) {
    if (refreshToken.getTokenType() != AuthTokenType.REFRESH) {
      throw new AuthException("El token proporcionado no es un token de actualización válido");
    }

    if (!jwtService.isSinged(refreshToken.getTokenHash())) {
      throw new AuthException("Tu sesión no es válida. Por favor, inicia sesión nuevamente");
    }

    if (!refreshToken.isValid())
      throw new AuthException("Tu sesión ya no es válida. Por favor, inicia sesión nuevamente");

    var user = userRepo.findById(refreshToken.getUserId()).orElseThrow(() ->
            new AuthException("Tu sesión no es válida. Por favor, inicia sesión nuevamente"));

    return createNewAccessTokenUseCase.execute(user);
  }
}
