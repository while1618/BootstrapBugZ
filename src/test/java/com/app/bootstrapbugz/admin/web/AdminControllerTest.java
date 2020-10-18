package com.app.bootstrapbugz.admin.web;

import com.app.bootstrapbugz.admin.dto.request.AdminRequest;
import com.app.bootstrapbugz.admin.dto.request.ChangeRoleRequest;
import com.app.bootstrapbugz.auth.dto.request.LoginRequest;
import com.app.bootstrapbugz.user.model.RoleName;
import com.app.bootstrapbugz.jwt.JwtUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    private static final AdminRequest ADMIN_REQUEST = new AdminRequest(Arrays.asList("not_activated", "locked"));
    private static final String PATH = "/api/admin";

    @BeforeAll
    void loginAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "123");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        adminToken = resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
    }

    @BeforeAll
    void loginNonAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "123");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        userToken = resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
    }

    @Test
    @Order(1)
    void logoutUserFromAllDevices_statusNoContent() throws Exception {
        mockMvc.perform(get(PATH + "/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(2)
    void logoutUserFromAllDevices_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(get(PATH + "/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void logoutUserFromAllDevices_statusUnauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void logoutUserFromAllDevices_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void changeUsersRole_statusNoContent() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(
                Collections.singletonList("not_activated"),
                Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put(PATH + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    void changeUsersRole_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void changeUsersRole_statusUnauthorized() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(
                Collections.singletonList("not_activated"),
                Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put(PATH + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    void changeUsersRole_adminNotLoggedIn_statusForbidden() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest(
                Collections.singletonList("not_activated"),
                Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put(PATH + "/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    void lockUsers_statusNoContent() throws Exception {
        mockMvc.perform(put(PATH + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(10)
    void lockUsers_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    void lockUsers_statusUnauthorized() throws Exception {
        mockMvc.perform(put(PATH + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(12)
    void lockUsers_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put(PATH + "/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    void unlockUsers_statusNoContent() throws Exception {
        mockMvc.perform(put(PATH + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(14)
    void unlockUsers_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    void unlockUsers_statusUnauthorized() throws Exception {
        mockMvc.perform(put(PATH + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    void unlockUsers_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put(PATH + "/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    void deactivateUser_statusNoContent() throws Exception {
        mockMvc.perform(put(PATH + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(18)
    void deactivateUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(19)
    void deactivateUser_statusUnauthorized() throws Exception {
        mockMvc.perform(put(PATH + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(20)
    void deactivateUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put(PATH + "/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(21)
    void activateUser_statusNoContent() throws Exception {
        mockMvc.perform(put(PATH + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(22)
    void activateUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(23)
    void activateUser_statusUnauthorized() throws Exception {
        mockMvc.perform(put(PATH + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(24)
    void activateUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put(PATH + "/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(25)
    void deleteUser_statusNoContent() throws Exception {
        mockMvc.perform(delete(PATH + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(26)
    void deleteUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(delete(PATH + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(27)
    void deleteUser_statusUnauthorized() throws Exception {
        mockMvc.perform(delete(PATH + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(28)
    void deleteUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(delete(PATH + "/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }
}
