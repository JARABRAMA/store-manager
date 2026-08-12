package com.jarabrama.store_manager.authentication.usecases;

import com.jarabrama.store_manager.authentication.domain.AuthTokenRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SessionRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.SystemUserRepositoryPort;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidCredentialsException;
import com.jarabrama.store_manager.authentication.domain.model.SystemUser;
import com.jarabrama.store_manager.authentication.domain.model.TrustedDevice;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginRequest;
import com.jarabrama.store_manager.authentication.presentation.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application use case that authenticates a user and starts a new session.
 * <p>
 * Coordinates credential validation, session revocation, trusted device handling
 * and the creation of access and refresh tokens on every login attempt.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LoginUseCase {

  private final SystemUserRepositoryPort userRepo;
  private final PasswordEncoder passwordEncoder;
  private final SessionRepositoryPort sessionRepo;
  private final AuthTokenRepositoryPort  authTokenRepo;

  private final CreateNewAccessTokenUseCase createNewAccessTokenUseCase;
  private final CreateRefreshTokenUseCase createRefreshTokenUseCase;
  private final FindOrCreateTrustedDeviceUseCase findOrCreateTrustedDeviceUseCase;
  private final CreateAndSaveNewSessionUseCase createAndSaveNewSessionUseCase;

  /**
   * Performs user authentication for the given login request.
   * <p>
   * Validates the supplied credentials, revokes any existing sessions for the user,
   * registers the device as trusted when requested, and issues a new access and
   * refresh token pair bound to a newly created session.
   * </p>
   *
   * @param request the login request containing the username, password and, optionally,
   *                the trusted device information.
   * @return the login response with the generated access and refresh token hashes.
   * @throws InvalidCredentialsException if the username does not exist or the password
   *                                     does not match.
   */
  @Transactional
  public LoginResponse execute(LoginRequest request) {
    var user = getUserAndValidateCredentials(request);
    sessionRepo.revokeAllByUser(user.getId());
    authTokenRepo.revokeAllByUser(user.getId());

    TrustedDevice trustedDevice = null;
    if (request.trustedDevice()) {
      trustedDevice = findOrCreateTrustedDeviceUseCase.execute(request.deviceHash(), user.getId());
    }

    var accessToken = createNewAccessTokenUseCase.execute(user);
    var refreshToken = createRefreshTokenUseCase.execute(user,
            trustedDevice != null ? trustedDevice.getId() : null);

    createAndSaveNewSessionUseCase.execute(user.getId(),
            trustedDevice != null ? trustedDevice.getId() : null,
            refreshToken);

    return new LoginResponse(accessToken.getTokenHash(), refreshToken.getTokenHash());
  }


  /**
   * Finds the user by username and verifies that the provided password matches
   * the stored password hash.
   *
   * @param request the login request with the credentials to validate.
   * @return the authenticated user.
   * @throws InvalidCredentialsException if the user is not found or the password
   *                                     is incorrect.
   */
  private SystemUser getUserAndValidateCredentials(LoginRequest request) {
    var user = userRepo.findByUsername(request.username())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));

    var correctPassword = passwordEncoder.matches(request.password(),
            user.getPasswordHash());

    if (!correctPassword) {
      throw new InvalidCredentialsException("Credenciales incorrectas");
    }
    return user;
  }
}