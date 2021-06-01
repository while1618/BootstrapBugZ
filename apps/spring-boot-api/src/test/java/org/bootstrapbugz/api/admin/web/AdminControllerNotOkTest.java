package org.bootstrapbugz.api.admin.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtilities;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.model.RoleName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerNotOkTest {
  private static final AdminRequest ADMIN_REQUEST =
      new AdminRequest(Set.of("not_activated", "locked"));
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private String adminToken;
  private String userToken;

  @BeforeAll
  void loginAdmin() throws Exception {
    LoginRequest loginRequest = new LoginRequest("admin", "qwerty123");
    ResultActions resultActions =
        mockMvc.perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
    adminToken = resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
  }

  @BeforeAll
  void loginNonAdmin() throws Exception {
    LoginRequest loginRequest = new LoginRequest("user", "qwerty123");
    ResultActions resultActions =
        mockMvc.perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
    userToken = resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
  }

  @Test
  void findAllUsers_unauthorized() throws Exception {
    mockMvc
        .perform(
            get(Path.ADMIN + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void findAllUsers_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(get(Path.ADMIN + "/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void changeRole_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changeRole_unauthorized() throws Exception {
    ChangeRoleRequest changeRoleRequest =
        new ChangeRoleRequest(Set.of("not_activated"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void changeRole_adminNotLoggedIn_forbidden() throws Exception {
    ChangeRoleRequest changeRoleRequest =
        new ChangeRoleRequest(Set.of("not_activated"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void lock_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void lock_unauthorized() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void lock_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void unlock_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void unlock_unauthorized() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void unlock_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void deactivate_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void deactivate_unauthorized() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deactivate_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void activate_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void activate_unauthorized() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void activate_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_unauthorized() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void delete_adminNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isForbidden());
  }
}
