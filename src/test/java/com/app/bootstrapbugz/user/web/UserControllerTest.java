package com.app.bootstrapbugz.user.web;

import com.app.bootstrapbugz.constant.JwtProperties;
import com.app.bootstrapbugz.dto.request.auth.LoginRequest;
import com.app.bootstrapbugz.dto.request.user.ChangePasswordRequest;
import com.app.bootstrapbugz.dto.request.user.EditUserRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private static final String PATH = "/api/users";

    @BeforeAll
    void init() throws Exception {
        login(new LoginRequest("user", "123"));
    }

    private void login(LoginRequest loginRequest) throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
        token = resultActions.andReturn().getResponse().getHeader(JwtProperties.HEADER);
    }

    @Test
    @Order(1)
    void findAllUsers_statusOk() throws Exception {
        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void findAllUsers_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void findUserByUsername_statusOk() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void findUserByUsername_statusNotFound() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void findUserByUsername_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/{username}", "user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    void changePassword_statusNoContent() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNoContent());
        login(new LoginRequest("user", changePasswordRequest.getNewPassword()));
    }

    @Test
    @Order(7)
    void changePassword_wrongOldPassword_statusBadRequest() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "12345", "12345");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void changePassword_passwordsDoNotMatch_statusBadRequest() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("1234", "12345", "123456");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void changePassword_userNotLoggedIn_statusForbidden() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        mockMvc.perform(put(PATH + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    void editUser_emailRemainsTheSame_statusOk() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "decrescendo807@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isOk());
        login(new LoginRequest("test", "1234"));
    }

    @Test
    @Order(11)
    void editUser_invalidParameters_statusBadRequest() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test2", "Test2", "test", "decrescendo807@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    void editUser_usernameExists_statusBadRequest() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "admin", "decrescendo807@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(13)
    void editUser_emailExists_statusBadRequest() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test2", "skill.potion21@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    void editUser_userNotLoggedIn_statusForbidden() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "decrescendo807@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(15)
    void logoutFromAllDevices_statusNoContent() throws Exception {
        mockMvc.perform(get(PATH + "/logout-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(16)
    void logoutFromAllDevices_userNotLoggedIn_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/logout-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    void tryToAccessResourceAfterLogout_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(18)
    void loginAfterLogout_statusOk() throws Exception {
        login(new LoginRequest("test", "1234"));
    }

    @Test
    @Order(19)
    void editUser_changeEmail_statusOk() throws Exception {
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "the.littlefinger63@gmail.com");
        mockMvc.perform(put(PATH + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProperties.HEADER, token)
                .content(objectMapper.writeValueAsString(editUserRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(20)
    void tryToLoginAfterEmailChanged_statusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("test", "1234"))))
                .andExpect(status().isUnauthorized());
    }
}
