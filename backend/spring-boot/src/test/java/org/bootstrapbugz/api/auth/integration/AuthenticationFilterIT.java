package org.bootstrapbugz.api.auth.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
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
class AuthenticationFilterIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void createAuthTokens() throws Exception {
    final var authTokensRequest = new AuthTokensRequest("user", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString());
  }

  @Test
  void createAuthTokens_throwUnauthorized_wrongCredentials() throws Exception {
    final var authTokensRequest = new AuthTokensRequest("wrong", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString("Invalid credentials.")));
  }

  @ParameterizedTest
  @CsvSource({
    "locked, User locked.",
    "deactivated1, User not activated.",
  })
  void createAuthTokens_throwForbidden_lockedDeactivatedUser(String username, String message)
      throws Exception {
    final var authTokensRequest = new AuthTokensRequest(username, "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(message)));
  }
}
