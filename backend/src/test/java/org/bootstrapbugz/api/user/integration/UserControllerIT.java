package org.bootstrapbugz.api.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
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
    var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
    mockMvc
        .perform(
            get(Path.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.getAccessToken()))
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
    var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var expectedUserDTO =
        new UserDTO(2L, "User", "User", "user", "user@bootstrapbugz.com", true, true, null);
    performFindUserByUsername("user", signInDTO.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserDTO)));
  }

  @Test
  void itShouldFindUserByUsername_hideEmail() throws Exception {
    var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var expectedUserDTO =
        new UserDTO(1L, "Admin", "Admin", "admin", null, true, true, null);
    performFindUserByUsername("admin", signInDTO.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserDTO)));
  }

  @Test
  void itShouldFindUserByUsername_adminSignedIn() throws Exception {
    var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("admin", "qwerty123"));
    var expectedUserDTO =
        new UserDTO(
            2L,
            "User",
            "User",
            "user",
            "user@bootstrapbugz.com",
            true,
            true,
            Set.of(new RoleDTO(Role.RoleName.USER.name())));
    performFindUserByUsername("user", signInDTO.getAccessToken())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(objectMapper.writeValueAsString(expectedUserDTO)));
  }

  @Test
  void findUserByUsernameShouldThrowResourceNotFound() throws Exception {
    var signInDTO =
        TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var resultActions =
        performFindUserByUsername("unknown", signInDTO.getAccessToken())
            .andExpect(status().isNotFound());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }
}
