package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.payload.dto.RoleDto;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
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
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.1", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "updated", "updated@bootstrapbugz.com");
    var roleResponses = Collections.singleton(new RoleDto(Role.RoleName.USER.name()));
    var expectedUserDto =
        new UserDto(
            5L,
            "Updated",
            "Updated",
            "updated",
            "updated@bootstrapbugz.com",
            false,
            true,
            roleResponses);
    performUpdateUser(updateUserRequest, signInDto.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserDto)));
  }

  @Test
  void itShouldUpdateUser_sameUsernameAndEmail() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest(
            "Updated", "Updated", "for.update.2", "for.update.2@bootstrapbugz.com");
    var roleResponses = Collections.singleton(new RoleDto(Role.RoleName.USER.name()));
    var expectedUserDto =
        new UserDto(
            6L,
            "Updated",
            "Updated",
            "for.update.2",
            "for.update.2@bootstrapbugz.com",
            true,
            true,
            roleResponses);
    performUpdateUser(updateUserRequest, signInDto.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserDto)));
  }

  @Test
  void updateUserShouldThrowBadRequest_usernameExists() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "user", "for.update.2@bootstrapbugz.com");
    var resultActions =
        performUpdateUser(updateUserRequest, signInDto.getAccessToken())
            .andExpect(status().isConflict());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.CONFLICT);
    expectedErrorResponse.addDetails("username", "Username already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_emailExists() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Updated", "Updated", "for.update.2", "user@bootstrapbugz.com");
    var resultActions =
        performUpdateUser(updateUserRequest, signInDto.getAccessToken())
            .andExpect(status().isConflict());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.CONFLICT);
    expectedErrorResponse.addDetails("email", "Email already exists.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_invalidParameters() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var updateUserRequest =
        new UpdateProfileRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    var resultActions =
        performUpdateUser(updateUserRequest, signInDto.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("firstName", "Invalid first name.");
    expectedErrorResponse.addDetails("lastName", "Invalid last name.");
    expectedErrorResponse.addDetails("username", "Invalid username.");
    expectedErrorResponse.addDetails("email", "Invalid email.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldChangePassword() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.3", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    performChangePassword(changePasswordRequest, signInDto.getAccessToken())
        .andExpect(status().isNoContent());
  }

  @Test
  void changePasswordShouldThrowBadRequest_oldPasswordDoNotMatch() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    var resultActions =
        performChangePassword(changePasswordRequest, signInDto.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("oldPassword", "Wrong old password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    var resultActions =
        performChangePassword(changePasswordRequest, signInDto.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_invalidParameters() throws Exception {
    var signInDto =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.2", "qwerty123"));
    var changePasswordRequest = new ChangePasswordRequest("invalid", "invalid", "invalid");
    var resultActions =
        performChangePassword(changePasswordRequest, signInDto.getAccessToken())
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("newPassword", "Invalid password.");
    expectedErrorResponse.addDetails("oldPassword", "Invalid password.");
    expectedErrorResponse.addDetails("confirmNewPassword", "Invalid password.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
