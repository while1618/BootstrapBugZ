package org.bootstrapbugz.api.admin.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.junit.jupiter.api.BeforeEach;
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
class AdminControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String accessToken;

  @BeforeEach
  void setUp() throws Exception {
    final var signInRequest = new SignInRequest("admin", "qwerty123");
    accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, signInRequest).accessToken();
  }

  @Test
  void updateUsersRoles() throws Exception {
    final var updateRoleRequest =
        new UpdateRoleRequest(Set.of("update1", "update2"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteUsers() throws Exception {
    final var adminRequest = new AdminRequest(Set.of("delete1", "delete2"));
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isNoContent());
  }

  @ParameterizedTest
  @CsvSource({
    "lock, update3",
    "unlock, locked",
    "deactivate, update4",
    "activate, deactivated",
  })
  void lockUnlockDeactivateActivateUsers(String path, String username) throws Exception {
    final var adminRequest = new AdminRequest(Set.of(username));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken)
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isNoContent());
  }
}
