package org.bootstrapbugz.api.shared.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.payload.request.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AccessingResourcesIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void updateProfile_throwUnauthorized_userNotSignedIn() throws Exception {
    final var updateUserRequest =
        new UpdateProfileRequest("User", "User", "user", "user@localhost");
    mockMvc
        .perform(
            put(Path.PROFILE + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changePassword_throwUnauthorized_userNotSignedIn() throws Exception {
    final var changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.PROFILE + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changeUsersRoles_throwUnauthorized_userNotSignedIn() throws Exception {
    final var updateRoleRequest =
        new UpdateRoleRequest(Collections.singleton("test"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changeUsersRoles_throwForbidden_userNotAdmin() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var updateRoleRequest =
        new UpdateRoleRequest(Collections.singleton("test"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, for.update.1",
    "activate, not.activated",
  })
  void lockUnlockDeactivateActivateUsers_throwUnauthorized_userNotSignedIn(
      String path, String username) throws Exception {
    final var adminRequest = new AdminRequest(Collections.singleton(username));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, for.update.1",
    "activate, not.activated",
  })
  void lockUnlockDeactivateActivateUsers_throwForbidden_userNotAdmin(String path, String username)
      throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var adminRequest = new AdminRequest(Collections.singleton(username));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteUsers_throwUnauthorized_userNotSignedIn() throws Exception {
    final var adminRequest = new AdminRequest(Collections.singleton("for.update.2"));
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deleteUsers_throwForbidden_userNotAdmin() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var adminRequest = new AdminRequest(Collections.singleton("for.update.2"));
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void receiveSignedInUser_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(get(Path.AUTH + "/signed-in-user").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signOut_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(post(Path.AUTH + "/sign-out").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signOutFromAllDevices_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out-from-all-devices").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
