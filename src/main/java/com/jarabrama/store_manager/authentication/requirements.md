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
5. If the session has no valid refresh token, no extension happens and the request
   is treated as unauthenticated.

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

### Scenario: User refreshes with an expired or revoked refresh token

- Given: the user is logged in
- And: the access token has expired
- When: the user requests a refresh with a refresh token that is expired or revoked
- Then: the system returns HTTP 401 Unauthorized
- And: a message telling the user to log in again

### Acceptance criteria

1. Endpoint: `POST /api/auth/refresh` receiving the refresh token in the request.
2. On success: returns a new access token with a 5-minute expiry. No new refresh
   token is issued and the existing one stays valid.
3. On success: the session's `lastActivityAt` and `expiresAt` are updated.
4. The presented token must be of type `REFRESH`, belong to an existing
   non-revoked session, and be neither expired nor revoked.
5. On expired, revoked or otherwise invalid tokens: HTTP 401 with a message such as
   "Session expired, please log in again".
6. Revoked refresh tokens must not be reusable.
