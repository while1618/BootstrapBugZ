package com.app.bootstrapbugz.auth.web;

import com.app.bootstrapbugz.dto.request.auth.ForgotPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.dto.request.auth.ResetPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signUp_statusCreated() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest()
                .setFirstName("Test")
                .setLastName("Test")
                .setUsername("test")
                .setEmail("test@localhost.com")
                .setPassword("123")
                .setConfirmPassword("123");
        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void signUp_invalidParameters_statusBadRequest() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest()
                .setFirstName("Test12")
                .setLastName("Test12")
                .setUsername("test")
                .setEmail("test@localhost.com")
                .setPassword("1234")
                .setConfirmPassword("123");
        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmRegistration_invalidToken_statusBadRequest() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
        mockMvc.perform(get("/api/auth/confirm-registration")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resendConfirmationEmail_userNotFount_statusNotFound() throws Exception {
        ResendConfirmationEmailRequest resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
        mockMvc.perform(post("/api/auth/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void resendConfirmationEmail_alreadyActivated_statusBadRequest() throws Exception {
        ResendConfirmationEmailRequest resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("user");
        mockMvc.perform(post("/api/auth/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPassword_statusNoContent() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("user@localhost.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_userNotFount_statusNotFound() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test@localhost.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void forgotPassword_invalidParameters_statusBadRequest() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_invalidToken_statusBadRequest() throws Exception {
        String invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(invalidToken, "123", "123");
        mockMvc.perform(put("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isBadRequest());
    }
}
