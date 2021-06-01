package org.bootstrapbugz.api.user.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtilities;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerNotOkTest {
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
  void findAll_userNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(get(Path.USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void findByUsername_notFound() throws Exception {
    mockMvc
        .perform(
            get(Path.USERS + "/{username}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token))
        .andExpect(status().isNotFound());
  }

  @Test
  void findByUsername_userNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(get(Path.USERS + "/{username}", "user").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void changePassword_wrongOldPassword_badRequest() throws Exception {
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("BlaBla123", "123BlaBla", "123BlaBla");
    mockMvc
        .perform(
            put(Path.USERS + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePassword_passwordsDoNotMatch_badRequest() throws Exception {
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "123BlaBla", "123BlaBla123");
    mockMvc
        .perform(
            put(Path.USERS + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changePassword_userNotLoggedIn_forbidden() throws Exception {
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "BlaBla123", "BlaBla123");
    mockMvc
        .perform(
            put(Path.USERS + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_invalidParameters_badRequest() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test2", "Test2", "test", "decrescendo807@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_usernameExists_badRequest() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "admin", "decrescendo807@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_emailExists_badRequest() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test2", "skill.potion21@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_userNotLoggedIn_forbidden() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "decrescendo807@gmail.com");
    mockMvc
        .perform(
            put(Path.USERS + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isForbidden());
  }
}
