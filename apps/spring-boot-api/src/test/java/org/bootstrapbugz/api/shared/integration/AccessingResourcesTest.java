package org.bootstrapbugz.api.shared.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AccessingResourcesTest extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private final ErrorResponse expectedForbiddenResponse =
      new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Forbidden");
  private final ErrorResponse expectedUnauthorizedResponse =
      new ErrorResponse(HttpStatus.UNAUTHORIZED, ErrorDomain.AUTH, "Access is denied");

  @Test
  void findUserByUsernameShouldThrowForbidden_userNotLogged() throws Exception {
    var resultActions =
        mockMvc
            .perform(
                get(Path.USERS + "/{username}", "unknown").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void updateUserShouldThrowForbidden_userNotLogged() throws Exception {
    var updateUserRequest =
        new UpdateUserRequest("Updated", "Updated", "forUpdate2", "forUpdate2@localhost.com");
    var resultActions =
        mockMvc
            .perform(
                put(Path.USERS + "/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowForbidden_userNotLogged() throws Exception {
    var changePasswordRequest = new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    var resultActions =
        mockMvc
            .perform(
                put(Path.USERS + "/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(changePasswordRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void findAllUsersShouldThrowForbidden_userNotLogged() throws Exception {
    var resultActions =
        mockMvc
            .perform(get(Path.ADMIN + "/users").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void findAllUsersShouldThrowUnauthorized_loggedUserIsNotAdmin() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    var resultActions =
        mockMvc
            .perform(
                get(Path.ADMIN + "/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, loginResponse.getToken()))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void changeUsersRolesShouldThrowForbidden_userNotLogged() throws Exception {
    var changeRoleRequest =
        new ChangeRoleRequest(Set.of("user"), Set.of(RoleName.USER, RoleName.ADMIN));
    var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(changeRoleRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void changeUsersRolesShouldThrowUnauthorized_loggedUserIsNotAdmin() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    var changeRoleRequest =
        new ChangeRoleRequest(Set.of("user"), Set.of(RoleName.USER, RoleName.ADMIN));
    var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, loginResponse.getToken())
                    .content(objectMapper.writeValueAsString(changeRoleRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, forUpdate1",
    "activate, notActivated",
  })
  void lockUnlockDeactivateActivateUsersShouldThrowForbidden_userNotLogged(
      String path, String username) throws Exception {
    var adminRequest = new AdminRequest(Set.of(username));
    var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/" + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, forUpdate1",
    "activate, notActivated",
  })
  void lockUnlockDeactivateActivateUsersShouldThrowUnauthorized_loggedUserIsNotAdmin(
      String path, String username) throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    var adminRequest = new AdminRequest(Set.of(username));
    var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/" + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, loginResponse.getToken())
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void deleteUsersShouldThrowForbidden_userNotLogged() throws Exception {
    var adminRequest = new AdminRequest(Set.of("forUpdate2"));
    var resultActions =
        mockMvc
            .perform(
                delete(Path.ADMIN + "/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void deleteUsersShouldThrowUnauthorized_loggedUserIsNotAdmin() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    var adminRequest = new AdminRequest(Set.of("forUpdate2"));
    var resultActions =
        mockMvc
            .perform(
                delete(Path.ADMIN + "/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, loginResponse.getToken())
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void logoutShouldThrowForbidden_userNotLogged() throws Exception {
    var resultActions =
        mockMvc
            .perform(get(Path.AUTH + "/logout").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void logoutFromAllDevicesShouldThrowForbidden_userNotLogged() throws Exception {
    var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/logout-from-all-devices").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }
}
