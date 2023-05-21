package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
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

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class UserControllerIT extends DatabaseContainers {
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
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
    mockMvc
        .perform(
            get(Path.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
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
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    performFindUserByUsername("user", signInDTO.accessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.firstName").value("User"));
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    performFindUserByUsername("admin", signInDTO.accessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username").value("admin"))
        .andExpect(jsonPath("$.firstName").value("Admin"));
  }

  @Test
  void itShouldFindUserByUsername_adminSignedIn() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
    performFindUserByUsername("user", signInDTO.accessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.firstName").value("User"));
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var resultActions =
        performFindUserByUsername("unknown", signInDTO.accessToken())
            .andExpect(status().isNotFound());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
