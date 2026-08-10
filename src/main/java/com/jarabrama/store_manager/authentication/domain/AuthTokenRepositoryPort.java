package com.jarabrama.store_manager.authentication.domain;

import com.jarabrama.store_manager.authentication.domain.model.AuthToken;

public interface AuthTokenRepositoryPort {
  AuthToken save(AuthToken authToken);
}
