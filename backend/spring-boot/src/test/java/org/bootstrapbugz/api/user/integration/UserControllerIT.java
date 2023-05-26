package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
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
class UserControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void findAllUsers_rolesAndEmailsHidden() throws Exception {
    mockMvc
        .perform(get(Path.USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].email").doesNotExist())
        .andExpect(jsonPath("$[0].roles").doesNotExist());
  }

  @Test
  void findAllUsers_rolesAndEmailsShown() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "admin").accessToken();
    mockMvc
        .perform(
            get(Path.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].email").exists())
        .andExpect(jsonPath("$[0].roles").exists());
  }

  @Test
  void findUserByUsername_emailShown() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    mockMvc
        .perform(
            get(Path.USERS + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.roles").doesNotExist());
  }

  @Test
  void findUserByUsername_emailHidden() throws Exception {
    final var accessToken = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user").accessToken();
    mockMvc
        .perform(
            get(Path.USERS + "/{username}", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.roles").doesNotExist());
  }

  @Test
  void findUserByUsername_adminSignedIn() throws Exception {
    final var accessToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "admin").accessToken();
    mockMvc
        .perform(
            get(Path.USERS + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.roles").exists());
  }

  @Test
  void findUserByUsername_throwResourceNotFound() throws Exception {
    mockMvc
        .perform(get(Path.USERS + "/{username}", "unknown").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
