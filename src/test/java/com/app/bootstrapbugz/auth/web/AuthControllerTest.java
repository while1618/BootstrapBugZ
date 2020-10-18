package com.app.bootstrapbugz.auth.web;

import com.app.bootstrapbugz.auth.dto.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.dto.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.SignUpRequest;
import com.app.bootstrapbugz.auth.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String PATH = "/api/auth";

    @Test
    @Order(1)
    void login_statusOk() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("user", "123"))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void login_badCredentials_statusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("test", "1234"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void login_userNotActivated_statusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("not_activated", "123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void login_userLocked_statusUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("locked", "123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void signUp_statusCreated() throws Exception {
        mockMvc.perform(post(PATH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctSignUpRequest())))
                .andExpect(status().isCreated());
    }

    private SignUpRequest correctSignUpRequest() {
        return new SignUpRequest()
                .setFirstName("Test")
                .setLastName("Test")
                .setUsername("test")
                .setEmail("the.littlefinger63@gmail.com")
                .setPassword("123")
                .setConfirmPassword("123");
    }

    @Test
    @Order(6)
    void signUp_invalidParameters_statusBadRequest() throws Exception {
        mockMvc.perform(post(PATH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignUpRequest())))
                .andExpect(status().isBadRequest());
    }

    private SignUpRequest invalidSignUpRequest() {
        return new SignUpRequest()
                .setFirstName("Test12")
                .setLastName("Test12")
                .setUsername("test")
                .setEmail("the.littlefinger63@gmail.com")
                .setPassword("1234")
                .setConfirmPassword("123");
    }

    @Test
    @Order(7)
    void confirmRegistration_invalidToken_statusForbidden() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
        mockMvc.perform(get(PATH + "/confirm-registration")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    void confirmRegistration_invalidToken_userNotFound_statusForbidden() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzIiwiZXhwIjoxNTk3MTY1OTY2fQ.jodYzNMMTcAr7v5r-aeTDs7qyxXOY625qAVBbptHsvbN1LBp5loe3s647LYAAupt9Eku-yyzNPty7d0lUYYnhA";
        mockMvc.perform(get(PATH + "/confirm-registration")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    void confirmRegistration_tokenNotProvided_statusForbidden() throws Exception {
        mockMvc.perform(get(PATH + "/confirm-registration")
                .param("token", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    void resendConfirmationEmail_statusNoContent() throws Exception {
        ResendConfirmationEmailRequest resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
        mockMvc.perform(post(PATH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(11)
    void resendConfirmationEmail_userNotFound_statusNotFound() throws Exception {
        ResendConfirmationEmailRequest resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("not_found");
        mockMvc.perform(post(PATH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void resendConfirmationEmail_alreadyActivated_statusForbidden() throws Exception {
        ResendConfirmationEmailRequest resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("user");
        mockMvc.perform(post(PATH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    void forgotPassword_statusNoContent() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("the.littlefinger63@gmail.com");
        mockMvc.perform(post(PATH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(14)
    void forgotPassword_userNotFound_statusNotFound() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("notFound@localhost.com");
        mockMvc.perform(post(PATH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    void forgotPassword_invalidParameters_statusBadRequest() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("notAnEmail");
        mockMvc.perform(post(PATH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(16)
    void resetPassword_invalidToken_statusForbidden() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(invalidToken, "123", "123");
        mockMvc.perform(put(PATH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    void resetPassword_invalidToken_userNotFound_statusForbidden() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzIiwiZXhwIjoxNTk3MTY1OTY2fQ.jodYzNMMTcAr7v5r-aeTDs7qyxXOY625qAVBbptHsvbN1LBp5loe3s647LYAAupt9Eku-yyzNPty7d0lUYYnhA";
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(invalidToken, "123", "123");
        mockMvc.perform(put(PATH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(18)
    void resetPassword_tokenNotProvided_statusForbidden() throws Exception {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("", "123", "123");
        mockMvc.perform(put(PATH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isForbidden());
    }
}
