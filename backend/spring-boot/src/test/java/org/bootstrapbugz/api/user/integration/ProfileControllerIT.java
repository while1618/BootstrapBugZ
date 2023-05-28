package org.bootstrapbugz.api.user.integration;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class ProfileControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private EmailService emailService;

  @Test
  void updateUser_newUsernameAndEmail() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update1").accessToken();
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "updated1", "updated1@localhost");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updated1"))
        .andExpect(jsonPath("$.email").value("updated1@localhost"));
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void updateUser_sameUsernameAndEmail() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update2").accessToken();
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "update2", "update2@localhost");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"))
        .andExpect(jsonPath("$.username").value("update2"))
        .andExpect(jsonPath("$.email").value("update2@localhost"));
  }

  @Test
  void updateUser_throwBadRequest_usernameExists() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "admin", "updated3@localhost");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Username already exists.")));
  }

  @Test
  void updateUser_throwBadRequest_emailExists() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "updated3", "admin@localhost");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Email already exists.")));
  }

  @Test
  void updateUser_throwBadRequest_invalidParameters() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var updateUserRequest =
        new UpdateProfileRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Invalid username.")))
        .andExpect(content().string(containsString("Invalid email.")));
  }

  @Test
  void changePassword() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update4").accessToken();
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.PROFILE + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void changePassword_throwBadRequest_wrongOldPassword() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.PROFILE + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Wrong old password.")));
  }

  @Test
  void changePassword_throwBadRequest_passwordsDoNotMatch() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    mockMvc
        .perform(
            put(Path.PROFILE + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void changePassword_throwBadRequest_invalidParameters() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update3").accessToken();
    final var changePasswordRequest = new ChangePasswordRequest("invalid", "invalid", "invalid");
    mockMvc
        .perform(
            put(Path.PROFILE + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("oldPassword")))
        .andExpect(content().string(containsString("newPassword")))
        .andExpect(content().string(containsString("confirmNewPassword")))
        .andExpect(content().string(containsString("Invalid password.")));
  }

  @Test
  void delete() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "delete1").accessToken();
    mockMvc
        .perform(
            put(Path.PROFILE + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isNoContent());
  }
}
