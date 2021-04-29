package com.app.bootstrapbugz.admin.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.bootstrapbugz.admin.request.AdminRequest;
import com.app.bootstrapbugz.admin.request.ChangeRoleRequest;
import com.app.bootstrapbugz.auth.request.LoginRequest;
import com.app.bootstrapbugz.common.constants.Path;
import com.app.bootstrapbugz.jwt.util.JwtUtilities;
import com.app.bootstrapbugz.user.model.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
class AdminControllerOkTest {
  private static final AdminRequest ADMIN_REQUEST =
      new AdminRequest(Set.of("not_activated", "locked"));
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private String adminToken;

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

  @Test
  @Order(1)
  void findAllUsers_ok() throws Exception {
    mockMvc
        .perform(
            get(Path.ADMIN + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  @Order(2)
  void changeRole_noContent() throws Exception {
    ChangeRoleRequest changeRoleRequest =
        new ChangeRoleRequest(Set.of("not_activated"), Set.of(RoleName.USER, RoleName.ADMIN));
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(3)
  void lock_noContent() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(4)
  void unlock_noContent() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(5)
  void deactivate_noContent() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(6)
  void activate_noContent() throws Exception {
    mockMvc
        .perform(
            put(Path.ADMIN + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(7)
  void delete_noContent() throws Exception {
    mockMvc
        .perform(
            delete(Path.ADMIN + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
        .andExpect(status().isNoContent());
  }
}
