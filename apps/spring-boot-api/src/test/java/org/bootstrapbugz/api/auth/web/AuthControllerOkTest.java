package org.bootstrapbugz.api.auth.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.jwt.util.JwtPurpose;
import org.bootstrapbugz.api.jwt.util.JwtUtilities;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
class AuthControllerOkTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtUtilities jwtUtilities;

  @Test
  @Order(1)
  void login_ok() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("decrescendo807@gmail.com", "qwerty123"))))
        .andExpect(status().isOk())
        .andExpect(header().exists(JwtUtilities.HEADER));
  }

  @Test
  @Order(2)
  void signUp_created() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctSignUpRequest())))
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.firstName").value("Test"))
        .andExpect(jsonPath("$.lastName").value("Test"))
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("the.littlefinger63@gmail.com"));
  }

  private SignUpRequest correctSignUpRequest() {
    return new SignUpRequest()
        .setFirstName("Test")
        .setLastName("Test")
        .setUsername("test")
        .setEmail("the.littlefinger63@gmail.com")
        .setPassword("qwerty123")
        .setConfirmPassword("qwerty123");
  }

  @Test
  @Order(3)
  void resendConfirmationEmail_noContent() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("the.littlefinger63@gmail.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(4)
  void confirmRegistration_userFromDB_noContent() throws Exception {
    User user = userRepository.findByUsername("test").orElseThrow();
    String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(5)
  void confirmRegistration_userGeneratedFromJava_noContent() throws Exception {
    User user = createUser();
    String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  private User createUser() {
    User user =
        new User()
            .setFirstName("Generated")
            .setLastName("Generated")
            .setUsername("generated")
            .setEmail("generated@localhost.com")
            .setPassword("qwerty123");
    Role role = new Role(1L, RoleName.USER);
    user.addRole(role);
    return userRepository.save(user);
  }

  @Test
  @Order(6)
  void forgotPassword_noContent() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest =
        new ForgotPasswordRequest("the.littlefinger63@gmail.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(7)
  void resetPassword_noContent() throws Exception {
    User user = userRepository.findByUsername("test").orElseThrow();
    String token = jwtUtilities.createToken(user, JwtPurpose.FORGOT_PASSWORD);
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(token, "BlaBla123", "BlaBla123");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(8)
  void logout_noContent() throws Exception {
    String token = login(new LoginRequest("user", "qwerty123"));
    mockMvc
        .perform(
            get(Path.AUTH + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtUtilities.HEADER, token))
        .andExpect(status().isNoContent());
  }

  private String login(LoginRequest loginRequest) throws Exception {
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    return resultActions.andReturn().getResponse().getHeader(JwtUtilities.HEADER);
  }
}
