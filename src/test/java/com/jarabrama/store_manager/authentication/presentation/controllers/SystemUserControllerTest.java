package com.jarabrama.store_manager.authentication.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarabrama.store_manager.authentication.presentation.dto.NewUserRequest;
import com.jarabrama.store_manager.authentication.usecases.SaveUserUseCase;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
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
}