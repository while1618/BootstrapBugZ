package org.bootstrapbugz.api.admin.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.auth.payload.dto.SignInDTO;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
class AdminControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private SignInDTO signInDTO;

  @BeforeAll
  void setUp() throws Exception {
    signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
  }

  @Test
  void itShouldUpdateUsersRoles() throws Exception {
    final var updateRoleRequest =
        new UpdateRoleRequest(Set.of("user"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/update-role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(updateRoleRequest)))
        .andExpect(status().isNoContent());
  }

  @ParameterizedTest
  @CsvSource({
    "lock, user",
    "unlock, locked",
    "deactivate, update1",
    "activate, deactivated",
  })
  void itShouldLockUnlockDeactivateActivateUsers(String path, String username) throws Exception {
    final var adminRequest = new AdminRequest(Set.of(username));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/" + path)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void itShouldDeleteUsers() throws Exception {
    final var adminRequest = new AdminRequest(Set.of("update2"));
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken())
                .content(objectMapper.writeValueAsString(adminRequest)))
        .andExpect(status().isNoContent());
  }
}
