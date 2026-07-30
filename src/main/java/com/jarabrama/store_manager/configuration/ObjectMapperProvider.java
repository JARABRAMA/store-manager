package com.jarabrama.store_manager.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperProvider {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
