# Authentication Requirements

## Session and token lifetimes

Sessions and refresh tokens expire based on the trust level of the device the user
logged in from:

| Device trust level | Refresh token / session lifetime |
| ------------------ | -------------------------------- |
| Trusted            | 5 days                           |
| Not trusted        | 15 minutes                       |

The access token always expires after 5 minutes.

## Feature: Extend session (sliding expiration)

When an authenticated user makes any request to the system, the session and its
refresh token are extended automatically (sliding window). The same refresh token
is reused; no new token is issued.

### Scenario: User with a session on a trusted device makes a request

- Given: the user is logged in and has an active session on a trusted device
- And: the session has a refresh token that is not revoked and not expired
- When: the user makes any authenticated request to the system
- Then: the system extends the refresh token and session expiration by 5 days from
  the moment of the request

### Scenario: User with a session on a not trusted device makes a request

- Given: the user is logged in and has an active session on a not trusted device
- And: the session has a refresh token that is not revoked and not expired
- When: the user makes any authenticated request to the system
- Then: the system extends the refresh token and session expiration by 15 minutes
  from the moment of the request

### Scenario: User has no session in the system

- Given: the user makes a request to the system
- And: the system attempts to extend the user's session
- When: there are no registered sessions for the user
- Then: the system throws a `SessionException`
- And: responds with HTTP 401 Unauthorized, prompting the user to log in

### Scenario: The user's last session has expired or been revoked

- Given: the user makes a request to the system
- And: the system attempts to extend the user's session
- When: the last session is expired or revoked
- Then: the system throws a `SessionException`
- And: responds with HTTP 401 Unauthorized, prompting the user to log in

### Acceptance criteria

1. Extension is triggered on every authenticated request (via a security filter or
   interceptor), not only at login.
2. Trust level is determined by the session having a `trustedDeviceId` set; a
   non-null value means trusted.
3. Only the last valid (not revoked, not expired) refresh token of the session is
   extended, and the same token is reused.
4. Extension recomputes the token `expiresAt` as `now + timeout` according to the
   trust level, and updates the session `lastActivityAt` to `now` and its
   `expiresAt` to the new token expiration.
5. If the user has no registered session, or the last session is expired or
   revoked, the system throws a `SessionException` and responds with HTTP 401
   Unauthorized, prompting the user to log in again. No extension is performed.

## Feature: Refresh token

Allows a user whose access token has expired to obtain a new one using the refresh
token.

### Scenario: User refreshes with a valid refresh token

- Given: the user is logged in
- And: the access token has expired
- When: the user requests a refresh with a refresh token that is not expired and
  not revoked
- Then: the system returns a new access token (5 minutes)
- And: the refresh token remains unchanged
- And: the session is extended

### Scenario: User sends a token that is not a refresh token

- Given: the user is logged in 
- And: the access token has expired
- When: the user request a refresh of his access token 
- And: sends a token that is not a refresh token 
- Then: throws and exception `AuthenticationException`
- And: response with HTTP 401 unauthorized, prompting the user to log in again.

### Scenario: User refreshes with an expired or revoked refresh token

- Given: the user is logged in
- And: the access token has expired
- When: the user requests a refresh with a refresh token that is expired or revoked
- Then: the system throws an `AuthenticationException`
- And: responds with HTTP 401 Unauthorized, prompting the user to log in again

### Scenario: The refresh token is not signed with the expected secret

- Given: the user requests a refresh of the access token
- And: the presented refresh token was not signed with the expected secret key
- When: the system tries to validate the token's signature
- Then: the system throws an `AuthenticationException`
- And: responds with HTTP 401 Unauthorized, prompting the user to log in again

### Scenario: The refresh token does not belong to any user
- Given: the user request a refresh of the access token
- And: the present refresh token is signed, not expired and not revoked
- When: the refresh token does not belong to any user in system
- Then: throws an `AuthenticationException`
- And: System response with HTTP 401 unauthorized, prompting the user to log in again

### Acceptance criteria

1. Endpoint: `POST /api/auth/refresh` receiving the refresh token in the request.
2. On success: returns a new access token with a 5-minute expiry. No new refresh
   token is issued and the existing one stays valid.
3. On success: the session's `lastActivityAt` and `expiresAt` are updated.
4. The presented token must be of type `REFRESH`, belong to an existing
   non-revoked session, and be neither expired nor revoked.
5. The token signature is verified first; a token not signed with the expected
   secret key must be rejected with HTTP 401.
6. On expired, revoked or otherwise invalid tokens: the system throws an
   `AuthenticationException` and responds with HTTP 401 with a message such as
   "Session expired, please log in again".
7. Revoked refresh tokens must not be reusable.

## Feature: Log out

Allows a user to end their session by revoking all of their active sessions, so
that their refresh tokens can no longer be used.

### Scenario: The user id is not found in the system

- Given: the user sends a logout request with a user id
- When: the user id is not present in the system
- Then: the system throws an `AuthException`
- And: responds with HTTP 401 Unauthorized, prompting the user to log in

### Scenario: The user id is valid

- Given: the user is logged in
- When: the user makes a logout request
- And: the user id is present in the system
- Then: all of the user's sessions are revoked
- And: the system confirms the logout so the user can be redirected to log in

### Acceptance criteria

1. Endpoint: `POST /api/auth/logout` receiving the user id in the request.
2. The user id must exist in the system; otherwise the system throws an
   `AuthException` and responds with HTTP 401, prompting the user to log in again.
3. On success, all of the user's sessions are revoked through the session
   repository, so their refresh tokens can no longer be used.
4. The access token is a short-lived stateless JWT and is not revoked; it remains
   valid until its 5-minute expiry.