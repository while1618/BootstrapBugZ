package com.app.bootstrapbugz.admin.web;

import com.app.bootstrapbugz.constant.JwtProperties;
import com.app.bootstrapbugz.dto.request.admin.AdminRequest;
import com.app.bootstrapbugz.dto.request.admin.ChangeRoleRequest;
import com.app.bootstrapbugz.dto.request.auth.LoginRequest;
import com.app.bootstrapbugz.model.user.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;
    private static final AdminRequest ADMIN_REQUEST = new AdminRequest(Collections.singletonList("user"));

    @BeforeAll
    void loginAdmin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "123");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        adminToken = resultActions.andReturn().getResponse().getHeader("Authorization");
    }

    @BeforeAll
    void loginUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "123");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        userToken = resultActions.andReturn().getResponse().getHeader("Authorization");
    }

    @Test
    void logoutUserFromAllDevices_statusNoContent() throws Exception {
        mockMvc.perform(get("/api/admin/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutUserFromAllDevices_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutUserFromAllDevices_statusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutUserFromAllDevices_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeUsersRole_statusNoContent() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest();
        changeRoleRequest.setUsernames(Collections.singletonList("user"));
        changeRoleRequest.setRoleNames(Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put("/api/admin/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeUsersRole_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeUsersRole_statusUnauthorized() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest();
        changeRoleRequest.setUsernames(Collections.singletonList("user"));
        changeRoleRequest.setRoleNames(Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put("/api/admin/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changeUsersRole_adminNotLoggedIn_statusForbidden() throws Exception {
        ChangeRoleRequest changeRoleRequest = new ChangeRoleRequest();
        changeRoleRequest.setUsernames(Collections.singletonList("user"));
        changeRoleRequest.setRoleNames(Arrays.asList(RoleName.ROLE_USER, RoleName.ROLE_ADMIN));
        mockMvc.perform(put("/api/admin/users/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRoleRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void lockUsers_statusNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void lockUsers_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lockUsers_statusUnauthorized() throws Exception {
        mockMvc.perform(put("/api/admin/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lockUsers_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put("/api/admin/users/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void unlockUsers_statusNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void unlockUsers_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unlockUsers_statusUnauthorized() throws Exception {
        mockMvc.perform(put("/api/admin/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unlockUsers_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put("/api/admin/users/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void activateUser_statusNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void activateUser_statusUnauthorized() throws Exception {
        mockMvc.perform(put("/api/admin/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void activateUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put("/api/admin/users/activate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deactivateUser_statusNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(put("/api/admin/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivateUser_statusUnauthorized() throws Exception {
        mockMvc.perform(put("/api/admin/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deactivateUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(put("/api/admin/users/deactivate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_statusNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(delete("/api/admin/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_statusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/admin/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, userToken)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_adminNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(delete("/api/admin/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ADMIN_REQUEST)))
                .andExpect(status().isForbidden());
    }
}
