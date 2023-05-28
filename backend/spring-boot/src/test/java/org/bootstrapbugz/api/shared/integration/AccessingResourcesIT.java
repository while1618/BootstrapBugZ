package org.bootstrapbugz.api.shared.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
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

  private final String unauthorized = "Full authentication is required to access this resource";
  private final String forbidden = "Access Denied";

  @Test
  void updateProfile_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(put(Path.PROFILE + "/update").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void changePassword_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(put(Path.PROFILE + "/change-password").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void changeUsersRoles_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/{username}/update-role", "update1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void changeUsersRoles_throwForbidden_userNotAdmin() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    final var updateRoleRequest = new UpdateRoleRequest(Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/{username}/update-role", "update1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
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
        .perform(
            put(Path.ADMIN + "/users/{username}/" + path, "update1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
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
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/{username}/" + path, username)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void deleteUsers_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/{username}/delete", "delete1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void deleteUsers_throwForbidden_userNotAdmin() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/{username}/delete", "delete1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void receiveSignedInUser_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(get(Path.AUTH + "/signed-in-user").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void signOut_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(post(Path.AUTH + "/sign-out").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void signOutFromAllDevices_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out-from-all-devices").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }
}
