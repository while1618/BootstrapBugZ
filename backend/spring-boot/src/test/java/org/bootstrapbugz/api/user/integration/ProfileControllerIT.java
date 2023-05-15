package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class ProfileControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private ResultActions performUpdateUser(UpdateProfileRequest updateProfileRequest, String token)
      throws Exception {
    return mockMvc.perform(
        put(Path.PROFILE + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token)
            .content(objectMapper.writeValueAsString(updateProfileRequest)));
  }

  private ResultActions performChangePassword(
      ChangePasswordRequest changePasswordRequest, String token) throws Exception {
    return mockMvc.perform(
        put(Path.PROFILE + "/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token)
            .content(objectMapper.writeValueAsString(changePasswordRequest)));
  }

  @Test
  void itShouldUpdateUser_newUsernameAndEmail() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.1", "qwerty123"));
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "updated", "updated@bootstrapbugz.com");
    performUpdateUser(updateUserRequest, signInDTO.accessToken())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updated"))
        .andExpect(jsonPath("$.firstName").value("Updated"));
  }

  @Test
  void itShouldUpdateUser_sameUsernameAndEmail() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var updateUserRequest =
        new UpdateProfileRequest(
            "Updated", "Updated", "for.update.2", "for.update.2@bootstrapbugz.com");
    performUpdateUser(updateUserRequest, signInDTO.accessToken())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("for.update.2"))
        .andExpect(jsonPath("$.firstName").value("Updated"));
  }

  @Test
  void updateUserShouldThrowBadRequest_usernameExists() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "user", "for.update.2@bootstrapbugz.com");
    final var resultActions =
        performUpdateUser(updateUserRequest, signInDTO.accessToken())
            .andExpect(status().isConflict());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.CONFLICT);
    expectedErrorResponse.addDetails("username", "Username already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_emailExists() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "for.update.2", "user@bootstrapbugz.com");
    final var resultActions =
        performUpdateUser(updateUserRequest, signInDTO.accessToken())
            .andExpect(status().isConflict());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.CONFLICT);
    expectedErrorResponse.addDetails("email", "Email already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_invalidParameters() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var updateUserRequest =
        new UpdateProfileRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    final var resultActions =
        performUpdateUser(updateUserRequest, signInDTO.accessToken())
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("firstName", "Invalid first name.");
    expectedErrorResponse.addDetails("lastName", "Invalid last name.");
    expectedErrorResponse.addDetails("username", "Invalid username.");
    expectedErrorResponse.addDetails("email", "Invalid email.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldChangePassword() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.3", "qwerty123"));
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    performChangePassword(changePasswordRequest, signInDTO.accessToken())
        .andExpect(status().isNoContent());
  }

  @Test
  void changePasswordShouldThrowBadRequest_oldPasswordDoNotMatch() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    final var resultActions =
        performChangePassword(changePasswordRequest, signInDTO.accessToken())
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("oldPassword", "Wrong old password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    final var resultActions =
        performChangePassword(changePasswordRequest, signInDTO.accessToken())
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_invalidParameters() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    final var changePasswordRequest = new ChangePasswordRequest("invalid", "invalid", "invalid");
    final var resultActions =
        performChangePassword(changePasswordRequest, signInDTO.accessToken())
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("newPassword", "Invalid password.");
    expectedErrorResponse.addDetails("oldPassword", "Invalid password.");
    expectedErrorResponse.addDetails("confirmNewPassword", "Invalid password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
