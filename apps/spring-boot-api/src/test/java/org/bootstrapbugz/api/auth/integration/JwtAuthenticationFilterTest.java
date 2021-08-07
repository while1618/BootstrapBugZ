package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class JwtAuthenticationFilterTest extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void itShouldLogin() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    assertThat(loginResponse.getUser().getUsername()).isEqualTo("user");
    assertThat(loginResponse.getToken()).isNotNull();
    assertThat(loginResponse.getRefreshToken()).isNotNull();
  }

  @Test
  void loginShouldThrowUnauthorized_wrongCredentials() throws Exception {
    var loginRequest = new LoginRequest("wrong", "qwerty123");
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.UNAUTHORIZED, ErrorDomain.AUTH, "Wrong username or password.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "locked, User locked.",
    "notActivated, User not activated.",
  })
  void loginShouldThrowForbidden_userLockedAndUserNotActivated(String username, String message)
      throws Exception {
    var loginRequest = new LoginRequest(username, "qwerty123");
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, message);
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
