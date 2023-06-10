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
  void signIn() throws Exception {
    final var signInRequest = new AuthTokensRequest("user", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.username").value("user"))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString());
  }

  @Test
  void signIn_throwUnauthorized_wrongCredentials() throws Exception {
    final var signInRequest = new AuthTokensRequest("wrong", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString("Invalid credentials.")));
  }

  @ParameterizedTest
  @CsvSource({
    "locked, User locked.",
    "deactivated, User not activated.",
  })
  void signIn_throwForbidden_lockedDeactivatedUser(String username, String message)
      throws Exception {
    final var signInRequest = new AuthTokensRequest(username, "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(message)));
  }
}
