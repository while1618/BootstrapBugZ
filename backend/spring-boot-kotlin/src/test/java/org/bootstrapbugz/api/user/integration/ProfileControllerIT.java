package org.bootstrapbugz.api.user.integration;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest;
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
  void findProfile() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    mockMvc
        .perform(
            get(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("user@localhost"));
  }

  @Test
  void patchProfile_newUsernameAndEmail() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update1");
    final var patchProfileRequest =
        new PatchProfileRequest("Updated", "Updated", "updated1", "updated1@localhost");
    mockMvc
        .perform(
            patch(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(patchProfileRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updated1"))
        .andExpect(jsonPath("$.email").value("updated1@localhost"));
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void patchProfile_sameUsernameAndEmail() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update2");
    final var patchProfileRequest = PatchProfileRequest.builder().firstName("Updated").build();
    mockMvc
        .perform(
            patch(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(patchProfileRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"))
        .andExpect(jsonPath("$.username").value("update2"))
        .andExpect(jsonPath("$.email").value("update2@localhost"));
  }

  @Test
  void patchProfile_throwConflict_usernameExists() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var patchProfileRequest = PatchProfileRequest.builder().username("admin").build();
    mockMvc
        .perform(
            patch(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(patchProfileRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Username already exists.")));
  }

  @Test
  void patchProfile_throwConflict_emailExists() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var patchProfileRequest = PatchProfileRequest.builder().email("admin@localhost").build();
    mockMvc
        .perform(
            patch(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(patchProfileRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Email already exists.")));
  }

  @Test
  void patchProfile_throwBadRequest_invalidParameters() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var patchProfileRequest =
        new PatchProfileRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    mockMvc
        .perform(
            patch(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(patchProfileRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Invalid username.")))
        .andExpect(content().string(containsString("Invalid email.")));
  }

  @Test
  void deleteProfile() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "delete1");
    mockMvc
        .perform(
            delete(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken())))
        .andExpect(status().isNoContent());
  }

  @Test
  void changePassword() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update4");
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            patch(Path.PROFILE + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void changePassword_throwBadRequest_wrongOldPassword() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            patch(Path.PROFILE + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Wrong current password.")));
  }

  @Test
  void changePassword_throwBadRequest_passwordsDoNotMatch() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    mockMvc
        .perform(
            patch(Path.PROFILE + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void changePassword_throwBadRequest_invalidParameters() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update3");
    final var changePasswordRequest = new ChangePasswordRequest("invalid", "invalid", "invalid");
    mockMvc
        .perform(
            patch(Path.PROFILE + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken()))
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("currentPassword")))
        .andExpect(content().string(containsString("newPassword")))
        .andExpect(content().string(containsString("confirmNewPassword")))
        .andExpect(content().string(containsString("Invalid password.")));
  }
}
