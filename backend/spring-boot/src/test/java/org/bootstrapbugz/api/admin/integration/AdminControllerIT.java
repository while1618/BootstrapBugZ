package org.bootstrapbugz.api.admin.integration;

import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AdminControllerIT extends DatabaseContainers {
  //  @Autowired private MockMvc mockMvc;
  //  @Autowired private ObjectMapper objectMapper;
  //
  //  private String accessToken;
  //
  //  @BeforeEach
  //  void setUp() throws Exception {
  //    accessToken = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "admin").accessToken();
  //  }
  //
  //  @Test
  //  void updateUserRoles() throws Exception {
  //    final var updateRoleRequest = new UpdateRoleRequest(Set.of(RoleName.USER, RoleName.ADMIN));
  //    mockMvc
  //        .perform(
  //            put(Path.ADMIN + "/users/{username}/update-role", "update1")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .headers(IntegrationTestUtil.authHeader(accessToken))
  //                .content(objectMapper.writeValueAsString(updateRoleRequest)))
  //        .andExpect(status().isNoContent());
  //  }
  //
  //  @Test
  //  void updateUserRoles_throwResourceNotFound_userNotFound() throws Exception {
  //    final var updateRoleRequest = new UpdateRoleRequest(Set.of(RoleName.USER, RoleName.ADMIN));
  //    mockMvc
  //        .perform(
  //            put(Path.ADMIN + "/users/{username}/update-role", "unknown")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .headers(IntegrationTestUtil.authHeader(accessToken))
  //                .content(objectMapper.writeValueAsString(updateRoleRequest)))
  //        .andExpect(status().isNotFound())
  //        .andExpect(content().string(containsString("User not found.")));
  //  }
  //
  //  @Test
  //  void deleteUser() throws Exception {
  //    mockMvc
  //        .perform(
  //            delete(Path.ADMIN + "/users/{username}/delete", "delete1")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .headers(IntegrationTestUtil.authHeader(accessToken)))
  //        .andExpect(status().isNoContent());
  //  }
  //
  //  @ParameterizedTest
  //  @CsvSource({
  //    "lock, update3",
  //    "unlock, locked",
  //    "deactivate, update4",
  //    "activate, deactivated",
  //  })
  //  void lockUnlockDeactivateActivateUser(String path, String username) throws Exception {
  //    mockMvc
  //        .perform(
  //            put(Path.ADMIN + "/users/{username}/" + path, username)
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .headers(IntegrationTestUtil.authHeader(accessToken)))
  //        .andExpect(status().isNoContent());
  //  }
  //
  //  @ParameterizedTest
  //  @CsvSource({
  //    "lock",
  //    "unlock",
  //    "deactivate",
  //    "activate",
  //  })
  //  void lockUnlockDeactivateActivateUser_throwResourceNotFound_userNotFound(String path)
  //      throws Exception {
  //    mockMvc
  //        .perform(
  //            put(Path.ADMIN + "/users/{username}/" + path, "unknown")
  //                .contentType(MediaType.APPLICATION_JSON)
  //                .headers(IntegrationTestUtil.authHeader(accessToken)))
  //        .andExpect(status().isNotFound())
  //        .andExpect(content().string(containsString("User not found.")));
  //  }
}
