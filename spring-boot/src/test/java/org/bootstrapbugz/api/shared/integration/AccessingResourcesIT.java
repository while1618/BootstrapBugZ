package org.bootstrapbugz.api.shared.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeAll;
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
class AccessingResourcesIT extends DatabaseContainers {
  private static ErrorMessage expectedForbiddenResponse;
  private static ErrorMessage expectedUnauthorizedResponse;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    expectedForbiddenResponse = new ErrorMessage(HttpStatus.FORBIDDEN);
    expectedForbiddenResponse.addDetails("Access Denied");
    expectedUnauthorizedResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED);
    expectedUnauthorizedResponse.addDetails(
        "Full authentication is required to access this resource");
  }

  @Test
  void updateProfileShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var updateUserRequest =
        new UpdateProfileRequest(
            "Updated", "Updated", "for.update.2", "for.update.2@bootstrapbugz.com");
    final var resultActions =
        mockMvc
            .perform(
                put(Path.PROFILE + "/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateUserRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void changePasswordShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    final var resultActions =
        mockMvc
            .perform(
                put(Path.PROFILE + "/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(changePasswordRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void changeUsersRolesShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var updateRoleRequest =
        new UpdateRoleRequest(Set.of("user"), Set.of(RoleName.USER, RoleName.ADMIN));
    final var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/update-role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRoleRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void changeUsersRolesShouldThrowForbidden_signedInUserIsNotAdmin() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var updateRoleRequest =
        new UpdateRoleRequest(Set.of("user"), Set.of(RoleName.USER, RoleName.ADMIN));
    final var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/update-role")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, signInDTO.getAccessToken())
                    .content(objectMapper.writeValueAsString(updateRoleRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, for.update.1",
    "activate, not.activated",
  })
  void lockUnlockDeactivateActivateUsersShouldThrowUnauthorized_userNotSignedIn(
      String path, String username) throws Exception {
    final var adminRequest = new AdminRequest(Set.of(username));
    final var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/" + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, for.update.1",
    "activate, not.activated",
  })
  void lockUnlockDeactivateActivateUsersShouldThrowForbidden_signedInUserIsNotAdmin(
      String path, String username) throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var adminRequest = new AdminRequest(Set.of(username));
    final var resultActions =
        mockMvc
            .perform(
                put(Path.ADMIN + "/users/" + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, signInDTO.getAccessToken())
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void deleteUsersShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var adminRequest = new AdminRequest(Set.of("for.update.2"));
    final var resultActions =
        mockMvc
            .perform(
                delete(Path.ADMIN + "/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void deleteUsersShouldThrowForbidden_signedInUserIsNotAdmin() throws Exception {
    final var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var adminRequest = new AdminRequest(Set.of("for.update.2"));
    final var resultActions =
        mockMvc
            .perform(
                delete(Path.ADMIN + "/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, signInDTO.getAccessToken())
                    .content(objectMapper.writeValueAsString(adminRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedForbiddenResponse, resultActions);
  }

  @Test
  void receiveSignedInUserShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var resultActions =
        mockMvc
            .perform(get(Path.AUTH + "/signed-in-user").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void signOutShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var resultActions =
        mockMvc
            .perform(post(Path.AUTH + "/sign-out").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }

  @Test
  void signOutFromAllDevicesShouldThrowUnauthorized_userNotSignedIn() throws Exception {
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-out-from-all-devices")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedUnauthorizedResponse, resultActions);
  }
}
