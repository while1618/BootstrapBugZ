package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.response.LoginResponse;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.bootstrapbugz.api.user.response.UserResponse.RoleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private ResultActions performFindUserByUsername(String username, String token) throws Exception {
    return mockMvc.perform(
        get(Path.USERS + "/{username}", username)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token));
  }

  private ResultActions performUpdateUser(UpdateUserRequest updateUserRequest, String token)
      throws Exception {
    return mockMvc.perform(
        put(Path.USERS + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token)
            .content(objectMapper.writeValueAsString(updateUserRequest)));
  }

  private ResultActions performChangePassword(
      ChangePasswordRequest changePasswordRequest, String token) throws Exception {
    return mockMvc.perform(
        put(Path.USERS + "/change-password")
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token)
            .content(objectMapper.writeValueAsString(changePasswordRequest)));
  }

  @Test
  void itShouldFindUserByUsername_showEmail() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    UserResponse expectedResponse =
        new UserResponse(2L, "User", "User", "user", "user@localhost.com", true, true, null);
    performFindUserByUsername("user", loginResponse.getToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    UserResponse expectedResponse =
        new UserResponse(1L, "Admin", "Admin", "admin", null, true, true, null);
    performFindUserByUsername("admin", loginResponse.getToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    ResultActions resultActions =
        performFindUserByUsername("unknown", loginResponse.getToken())
            .andExpect(status().isNotFound());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.USER, "User not found.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldUpdateUser_newUsernameAndEmail() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate1", "qwerty123"));
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Updated", "Updated", "updated", "updated@localhost.com");
    Set<RoleResponse> roles = Collections.singleton(new RoleResponse(RoleName.USER.name()));
    UserResponse expectedResponse =
        new UserResponse(
            5L, "Updated", "Updated", "updated", "updated@localhost.com", false, true, roles);
    performUpdateUser(updateUserRequest, loginResponse.getToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  void itShouldUpdateUser_sameUsernameAndEmail() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Updated", "Updated", "forUpdate2", "forUpdate2@localhost.com");
    Set<RoleResponse> roles = Collections.singleton(new RoleResponse(RoleName.USER.name()));
    UserResponse expectedResponse =
        new UserResponse(
            6L, "Updated", "Updated", "forUpdate2", "forUpdate2@localhost.com", true, true, roles);
    performUpdateUser(updateUserRequest, loginResponse.getToken())
        .andExpect(status().isOk())
        .andExpect(content().string(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  void updateUserShouldThrowBadRequest_usernameExists() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Updated", "Updated", "user", "forUpdate2@localhost.com");
    ResultActions resultActions =
        performUpdateUser(updateUserRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.BAD_REQUEST, ErrorDomain.USER, "Username already exists.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_emailExists() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Updated", "Updated", "forUpdate2", "user@localhost.com");
    ResultActions resultActions =
        performUpdateUser(updateUserRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.BAD_REQUEST, ErrorDomain.USER, "Email already exists.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowBadRequest_invalidParameters() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Invalid123", "Invalid123", "invalid#$%", "invalid");
    ResultActions resultActions =
        performUpdateUser(updateUserRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedResponse.addError("firstName", "Invalid first name.");
    expectedResponse.addError("lastName", "Invalid last name.");
    expectedResponse.addError("username", "Invalid username.");
    expectedResponse.addError("email", "Invalid email.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldChangePassword() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("admin", "qwerty123"));
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    performChangePassword(changePasswordRequest, loginResponse.getToken())
        .andExpect(status().isNoContent());
  }

  @Test
  void changePasswordShouldThrowBadRequest_oldPasswordDoNotMatch() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty12345", "qwerty1234", "qwerty1234");
    ResultActions resultActions =
        performChangePassword(changePasswordRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.BAD_REQUEST, ErrorDomain.USER, "Wrong old password.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty12345");
    ResultActions resultActions =
        performChangePassword(changePasswordRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedResponse.addError("newPassword", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowBadRequest_invalidParameters() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate2", "qwerty123"));
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("invalid", "invalid", "invalid");
    ResultActions resultActions =
        performChangePassword(changePasswordRequest, loginResponse.getToken())
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedResponse.addError("newPassword", "Invalid password.");
    expectedResponse.addError("oldPassword", "Invalid password.");
    expectedResponse.addError("confirmNewPassword", "Invalid password.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }
}
