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
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
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
    mockMvc
        .perform(put(Path.PROFILE + "/update").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changePassword_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(put(Path.PROFILE + "/change-password").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changeUsersRoles_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(put(Path.ADMIN + "/users/update-role").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changeUsersRoles_throwForbidden_userNotAdmin() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    final var updateRoleRequest =
        UpdateRoleRequest.builder()
            .usernames(Collections.singleton("update1"))
            .roleNames(Set.of(RoleName.USER, RoleName.ADMIN))
            .build();
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @CsvSource({
    "lock",
    "unlock",
    "deactivate",
    "activate",
  })
  void lockUnlockDeactivateActivateUsers_throwUnauthorized_userNotSignedIn(String path)
      throws Exception {
    mockMvc
        .perform(put(Path.ADMIN + "/users/" + path).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @CsvSource({
    "lock, update1",
    "unlock, locked",
    "deactivate, update2",
    "activate, deactivated",
  })
  void lockUnlockDeactivateActivateUsers_throwForbidden_userNotAdmin(String path, String username)
      throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    final var adminRequest = new AdminRequest(Collections.singleton(username));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteUsers_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(delete(Path.ADMIN + "/users/delete").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deleteUsers_throwForbidden_userNotAdmin() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    final var adminRequest = new AdminRequest(Collections.singleton("delete1"));
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
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
