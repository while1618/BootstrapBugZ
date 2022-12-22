package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
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
class AuthenticationFilterIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void itShouldSignIn() throws Exception {
    var signInDto = TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    assertThat(signInDto.getUser().getUsername()).isEqualTo("user");
    assertThat(signInDto.getAccessToken()).isNotNull();
    assertThat(signInDto.getRefreshToken()).isNotNull();
  }

  @Test
  void signInShouldThrowUnauthorized_wrongCredentials() throws Exception {
    var signInRequest = new SignInRequest("wrong", "qwerty123");
    var expectedErrorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED);
    expectedErrorResponse.addDetails("Invalid credentials.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @ParameterizedTest
  @CsvSource({
    "locked, User locked.",
    "not.activated, User not activated.",
  })
  void signInShouldThrowForbidden_userLockedAndUserNotActivated(String username, String message)
      throws Exception {
    var signInRequest = new SignInRequest(username, "qwerty123");
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails(message);
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
