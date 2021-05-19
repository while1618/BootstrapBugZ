package com.app.api.user.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.api.auth.request.LoginRequest;
import com.app.api.jwt.util.JwtUtilities;
import com.app.api.shared.constants.Path;
import com.app.api.user.request.ChangePasswordRequest;
import com.app.api.user.request.UpdateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
class UserControllerOkTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String token;

  @BeforeAll
  void init() throws Exception {
    login(new LoginRequest("user", "qwerty123"));
  }

  private void login(LoginRequest loginRequest) throws Exception {
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    token = resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
  }

  @Test
  @Order(1)
  void dummyTestForMaven() {
    assertTrue(true);
  }

  @Test
  @Order(2)
  void findAll_ok() throws Exception {
    mockMvc
        .perform(
            get(Path.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @Order(3)
  void findByUsername_ok() throws Exception {
    mockMvc
        .perform(
            get(Path.USERS + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("decrescendo807@gmail.com"));
  }

  @Test
  @Order(4)
  void changePassword_noContent() throws Exception {
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "BlaBla123", "BlaBla123");
    mockMvc
        .perform(
            put(Path.USERS + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isNoContent());
    login(new LoginRequest("user", changePasswordRequest.getNewPassword()));
  }

  @Test
  @Order(5)
  void update_emailRemainsTheSame_ok() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "decrescendo807@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("decrescendo807@gmail.com"));
    login(new LoginRequest("test", "BlaBla123"));
  }

  @Test
  @Order(6)
  void update_changeEmail_ok() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "the.littlefinger63@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("the.littlefinger63@gmail.com"));
  }

  @Test
  @Order(7)
  void tryToLoginAfterEmailChanged_unauthorized() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("test", "BlaBla123"))))
        .andExpect(status().isUnauthorized());
  }
}
