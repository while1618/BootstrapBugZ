package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.response.LoginResponse;
import org.bootstrapbugz.api.shared.config.RedisTestConfig;
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
import org.springframework.test.web.servlet.ResultActions;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = RedisTestConfig.class)
class JwtAuthenticationFilterTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void itShouldLogin() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    assertThat(loginResponse.getUser().getUsername()).isEqualTo("user");
    assertThat(loginResponse.getToken()).isNotNull();
    assertThat(loginResponse.getRefreshToken()).isNotNull();
  }

  @ParameterizedTest
  @CsvSource({
    "wrong, Wrong username or password.",
    "locked, User locked.",
    "notActivated, User not activated.",
  })
  void loginShouldThrowUnauthorized_wrongCredentialsUserLockedUserNotActivated(
      String username, String message) throws Exception {
    LoginRequest loginRequest = new LoginRequest(username, "qwerty123");
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.UNAUTHORIZED, ErrorDomain.AUTH, message);
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }
}
