package org.bootstrapbugz.api.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class UserControllerTest extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void itShouldFindAllUsersWithoutRolesAndEmails() throws Exception {
    mockMvc
        .perform(get(Path.USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(7))
        .andExpect(jsonPath("$[0].email").doesNotExist())
        .andExpect(jsonPath("$[0].roles").doesNotExist());
  }

  @Test
  void itShouldFindAllUsersWithRolesAndEmails() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
    mockMvc
        .perform(
            get(Path.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInResponse.getAccessToken()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(7))
        .andExpect(jsonPath("$[0].email").exists())
        .andExpect(jsonPath("$[0].roles").exists());
  }

  private ResultActions performFindUserByUsername(String username, String token) throws Exception {
    return mockMvc.perform(
        get(Path.USERS + "/{username}", username)
            .contentType(MediaType.APPLICATION_JSON)
            .header(AuthUtil.AUTH_HEADER, token));
  }

  @Test
  void itShouldFindUserByUsername_showEmail() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var expectedUserResponse =
        new UserResponse(2L, "User", "User", "user", "user@localhost.com", true, true, null);
    performFindUserByUsername("user", signInResponse.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserResponse)));
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var expectedUserResponse =
        new UserResponse(1L, "Admin", "Admin", "admin", null, true, true, null);
    performFindUserByUsername("admin", signInResponse.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserResponse)));
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() throws Exception {
    var signInResponse =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var resultActions =
        performFindUserByUsername("unknown", signInResponse.getAccessToken())
            .andExpect(status().isNotFound());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.USER, "User not found.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
