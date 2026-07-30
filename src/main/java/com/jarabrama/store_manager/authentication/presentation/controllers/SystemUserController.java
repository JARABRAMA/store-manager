package com.jarabrama.store_manager.authentication.presentation.controllers;

import com.jarabrama.store_manager.authentication.presentation.dto.NewUserRequest;
import com.jarabrama.store_manager.authentication.usecases.SaveUserUseCase;
import com.jarabrama.store_manager.inventory.application.model.dtos.SimpleResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class SystemUserController {
  private final SaveUserUseCase saveUserUseCase;

  @PostMapping()
  public ResponseEntity<SimpleResponse> save(@RequestBody NewUserRequest req) {
    var message = saveUserUseCase.execute(req);
    return new ResponseEntity<>(new SimpleResponse(message), HttpStatus.CREATED);
  }
}
