package com.jarabrama.store_manager.authentication.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarabrama.store_manager.authentication.domain.exceptions.DatabaseException;
import com.jarabrama.store_manager.authentication.domain.exceptions.InvalidNewUserException;
import com.jarabrama.store_manager.authentication.domain.exceptions.UserAlreadyExistsException;
import com.jarabrama.store_manager.authentication.presentation.dto.NewUserRequest;
import com.jarabrama.store_manager.authentication.usecases.SaveUserUseCase;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemUserController.class)
class SystemUserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SaveUserUseCase saveUserUseCase;

  @Mock
  private ObjectMapper objectMapper;

  @Test
  void should_return_message_with_202_status_when_success() throws Exception {
    var user = new NewUserRequest("admin", "password");
    var mapper = new ObjectMapper();
    when(saveUserUseCase.execute(any())).thenReturn("product saved");

    mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .content(mapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message")
                    .value("product saved"));
  }

  @Test
  void should_return_error_response_with_500_when_database_error() throws Exception {
    var mapper = new ObjectMapper();
    var errorMessage = "error message";
    var request = new NewUserRequest("admin", "password");
    when(saveUserUseCase.execute(any())).thenThrow(new DatabaseException(errorMessage));

    mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(errorMessage));
  }

  @Test
  void should_return_error_response_with_bad_request_status_when_invalid_new_user() throws Exception {
    var mapper = new ObjectMapper();
    var errorMessage = "error message";
    var request = new NewUserRequest("admin", "password");
    when(saveUserUseCase.execute(any())).thenThrow(new InvalidNewUserException(errorMessage));

    mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  void should_return_error_response_with_bad_request_status_when_username_already_exists() throws Exception {
    var mapper = new ObjectMapper();
    var errorMessage = "error message";
    var request = new NewUserRequest("admin", "password");

    when(saveUserUseCase.execute(any())).thenThrow(new UserAlreadyExistsException(errorMessage));

    mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON.toString())
                    .content(mapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorMessage))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));

  }
}