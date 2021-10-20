package org.bootstrapbugz.api.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.bootstrapbugz.api.user.payload.response.RoleResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
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

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class ProfileControllerTest extends DatabaseContainers {
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
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate1", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "updated", "updated@localhost.com");
    var roleResponses = Collections.singleton(new RoleResponse(Role.RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(
            5L,
            "Updated",
            "Updated",
            "updated",
            "updated@localhost.com",
            false,
            true,
            roleResponses);
    performUpdateUser(updateUserRequest, signInResponse.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserResponse)));
  }

  @Test
  void itShouldUpdateUser_sameUsernameAndEmail() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "forUpdate2", "forUpdate2@localhost.com");
    var roleResponses = Collections.singleton(new RoleResponse(Role.RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(
            6L,
            "Updated",
            "Updated",
            "forUpdate2",
            "forUpdate2@localhost.com",
            true,
            true,
            roleResponses);
    performUpdateUser(updateUserRequest, signInResponse.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserResponse)));
  }

  @Test
  void updateUserShouldThrowBadRequest_usernameExists() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "user", "forUpdate2@localhost.com");
    var resultActions =
        performUpdateUser(updateUserRequest, signInResponse.getAccessToken())
            .andExpect(status().isConflict());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.CONFLICT, ErrorDomain.USER, "Username already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_emailExists() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "forUpdate2", "user@localhost.com");
    var resultActions =
        performUpdateUser(updateUserRequest, signInResponse.getAccessToken())
            .andExpect(status().isConflict());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.CONFLICT, ErrorDomain.USER, "Email already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_invalidParameters() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    var resultActions =
        performUpdateUser(updateUserRequest, signInResponse.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addError("firstName", "Invalid first name.");
    expectedErrorResponse.addError("lastName", "Invalid last name.");
    expectedErrorResponse.addError("username", "Invalid username.");
    expectedErrorResponse.addError("email", "Invalid email.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldChangePassword() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate3", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    performChangePassword(changePasswordRequest, signInResponse.getAccessToken())
        .andExpect(status().isNoContent());
  }

  @Test
  void changePasswordShouldThrowBadRequest_oldPasswordDoNotMatch() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    var resultActions =
        performChangePassword(changePasswordRequest, signInResponse.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.BAD_REQUEST, ErrorDomain.USER, "Wrong old password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    var resultActions =
        performChangePassword(changePasswordRequest, signInResponse.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addError("newPassword", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_invalidParameters() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("forUpdate2", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("invalid", "invalid", "invalid");
    var resultActions =
        performChangePassword(changePasswordRequest, signInResponse.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addError("newPassword", "Invalid password.");
    expectedErrorResponse.addError("oldPassword", "Invalid password.");
    expectedErrorResponse.addError("confirmNewPassword", "Invalid password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
