package org.bootstrapbugz.api.shared.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.response.LoginResponse;
import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.user.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class TestUtil {
  private TestUtil() {}

  public static void checkErrorMessages(ErrorResponse expectedResponse, ResultActions resultActions)
      throws Exception {
    JSONObject actualResponse =
        new JSONObject(resultActions.andReturn().getResponse().getContentAsString());
    assertThat(actualResponse.getInt("status")).isEqualTo(expectedResponse.getStatus());
    assertThat(actualResponse.getString("error")).isEqualTo(expectedResponse.getError());
    assertThat(actualResponse.getJSONArray("errors"))
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(new JSONArray(expectedResponse.getErrors().toString()));
  }

  public static LoginResponse login(
      MockMvc mockMvc, ObjectMapper objectMapper, LoginRequest loginRequest) throws Exception {
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    return objectMapper.readValue(
        resultActions.andReturn().getResponse().getContentAsString(), LoginResponse.class);
  }

  public static void setAuth(Authentication auth, SecurityContext securityContext, User user) {
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(user));
  }
}
