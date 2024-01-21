package org.bootstrapbugz.api.shared.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.junit.jupiter.api.Test;
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
  void getProfile_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(get(Path.PROFILE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void patchProfile_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(patch(Path.PROFILE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void deleteProfile_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(delete(Path.PROFILE).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void changePassword_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(patch(Path.PROFILE + "/password").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void createUser_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(post(Path.ADMIN_USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void createUser_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    final var userRequest =
        new UserRequest(
            "test",
            "test",
            "test",
            "test@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));
    mockMvc
        .perform(
            post(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken()))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void getUsers_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(get(Path.ADMIN_USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void getUsers_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    mockMvc
        .perform(
            get(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken())))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void getUserById_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(get(Path.ADMIN_USERS + "/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void getUserById_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    mockMvc
        .perform(
            get(Path.ADMIN_USERS + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken())))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void updateUserById_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(put(Path.ADMIN_USERS + "/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void updateUserById_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    final var userRequest =
        new UserRequest(
            "test",
            "test",
            "test",
            "test@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));
    mockMvc
        .perform(
            put(Path.ADMIN_USERS + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken()))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void patchUserById_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(patch(Path.ADMIN_USERS + "/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void patchUserById_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    final var patchUserRequest =
        new PatchUserRequest(null, null, null, null, null, null, null, null, null);
    mockMvc
        .perform(
            patch(Path.ADMIN_USERS + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken()))
                .content(objectMapper.writeValueAsString(patchUserRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }

  @Test
  void deleteUserById_throwUnauthorized_userNotSignedIn() throws Exception {
    mockMvc
        .perform(delete(Path.ADMIN_USERS + "/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(unauthorized)));
  }

  @Test
  void deleteUserById_throwForbidden_userNotAdmin() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "user");
    mockMvc
        .perform(
            delete(Path.ADMIN_USERS + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.getAccessToken())))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(forbidden)));
  }
}
