package org.bootstrapbugz.api.admin.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
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

  private String accessToken;

  @BeforeEach
  void setUp() throws Exception {
    accessToken = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "admin").getAccessToken();
  }

  @Test
  void createUser() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "test1",
            "test1@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));
    mockMvc
        .perform(
            post(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  void createUser_throwBadRequest_invalidParameters() throws Exception {
    final var userRequest =
        new UserRequest(
            "Invalid123",
            "Invalid123",
            "invalid#$%",
            "invalid",
            "qwerty123",
            "qwerty1234",
            true,
            false,
            Set.of(RoleName.USER));
    mockMvc
        .perform(
            post(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Invalid username.")))
        .andExpect(content().string(containsString("Invalid email.")))
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void createUser_throwConflict_usernameExists() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "admin",
            "test2@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            post(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Username already exists.")));
  }

  @Test
  void createUser_throwConflict_emailExists() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "test2",
            "admin@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            post(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Email already exists.")));
  }

  @Test
  void findAllUsers() throws Exception {
    mockMvc
        .perform(
            get(Path.ADMIN_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(10));
  }

  @Test
  void findUserById() throws Exception {
    final var expectedUserDTO =
        new UserDTO(
            2L,
            "User",
            "User",
            "user",
            "user@localhost",
            true,
            false,
            Set.of(new RoleDTO(RoleName.USER.name())),
            LocalDateTime.now());
    final var response =
        mockMvc
            .perform(
                get(Path.ADMIN_USERS + "/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(IntegrationTestUtil.authHeader(accessToken)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var actualUserDTO = objectMapper.readValue(response, UserDTO.class);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserById_throwResourceNotFound() throws Exception {
    mockMvc
        .perform(
            get(Path.ADMIN_USERS + "/{id}", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("User not found.")));
  }

  @Test
  void updateUser() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "test3",
            "test3@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            put(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void updateUser_throwBadRequest_invalidParameters() throws Exception {
    final var userRequest =
        new UserRequest(
            "Invalid123",
            "Invalid123",
            "invalid#$%",
            "invalid",
            "qwerty123",
            "qwerty1234",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            put(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Invalid username.")))
        .andExpect(content().string(containsString("Invalid email.")))
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void updateUser_throwConflict_usernameExists() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "admin",
            "test2@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            put(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Username already exists.")));
  }

  @Test
  void updateUser_throwConflict_emailExists() throws Exception {
    final var userRequest =
        new UserRequest(
            "Test",
            "Test",
            "test2",
            "admin@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            put(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(userRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Email already exists.")));
  }

  @Test
  void patchUser() throws Exception {
    final var patchUserRequest =
        new UserRequest(
            "Test",
            "Test",
            "test4",
            "test4@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            patch(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(patchUserRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void patchUser_throwBadRequest_invalidParameters() throws Exception {
    final var patchUserRequest =
        new UserRequest(
            "Invalid123",
            "Invalid123",
            "invalid#$%",
            "invalid",
            "qwerty123",
            "qwerty1234",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            patch(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(patchUserRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Invalid username.")))
        .andExpect(content().string(containsString("Invalid email.")))
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void patchUser_throwConflict_usernameExists() throws Exception {
    final var patchUserRequest =
        new UserRequest(
            "Test",
            "Test",
            "admin",
            "test2@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            patch(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(patchUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Username already exists.")));
  }

  @Test
  void patchUser_throwConflict_emailExists() throws Exception {
    final var patchUserRequest =
        new PatchUserRequest(
            "Test",
            "Test",
            "test2",
            "admin@localhost",
            "qwerty123",
            "qwerty123",
            true,
            false,
            Set.of(RoleName.USER));

    mockMvc
        .perform(
            patch(Path.ADMIN_USERS + "/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken))
                .content(objectMapper.writeValueAsString(patchUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("Email already exists.")));
  }

  @Test
  void deleteUser() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN_USERS + "/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken)))
        .andExpect(status().isNoContent());
  }
}
