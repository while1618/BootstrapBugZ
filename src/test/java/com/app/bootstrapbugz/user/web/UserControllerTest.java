package com.app.bootstrapbugz.user.web;

import com.app.bootstrapbugz.constant.JwtProperties;
import com.app.bootstrapbugz.dto.request.auth.LoginRequest;
import com.app.bootstrapbugz.dto.request.user.ChangePasswordRequest;
import com.app.bootstrapbugz.dto.request.user.EditUserRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private static final String PATH = "/api/users";

    @BeforeAll
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "123");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        token = resultActions.andReturn().getResponse().getHeader(JwtProperties.HEADER);
    }

    @Test
    void findAllUsers_statusOk() throws Exception {
        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isOk());
    }

    @Test
    void findAllUsers_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void findUserByUsername_statusOk() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isOk());
    }

    @Test
    void findUserByUsername_statusNotFound() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isNotFound());
    }

    @Test
    void findUserByUsername_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePassword_statusNoContent() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_wrongOldPassword_statusBadRequest() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("12345", "1234", "1234");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_passwordsDoNotMatch_statusBadRequest() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "12345");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_userNotLoggedIn_statusForbidden() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void editUser_statusOk() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "the.littlefinger63@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void editUser_invalidParameters_statusBadRequest() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test2", "Test2", "test", "the.littlefinger63@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUser_userNotLoggedIn_statusForbidden() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "the.littlefinger63@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void logoutFromAllDevices_statusNoContent() throws Exception {
        mockMvc.perform(get(PATH + "/logout-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutFromAllDevices_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/logout-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
