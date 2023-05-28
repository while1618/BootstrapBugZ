package org.bootstrapbugz.api.shared.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.payload.dto.SignInDTO;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class IntegrationTestUtil {
  private IntegrationTestUtil() {}

  public static void checkErrorMessages(ErrorMessage expectedResponse, ResultActions resultActions)
      throws Exception {
    final var actualResponse =
        new JSONObject(resultActions.andReturn().getResponse().getContentAsString());
    assertThat(actualResponse.getInt("status")).isEqualTo(expectedResponse.getStatus());
    assertThat(actualResponse.getString("error")).isEqualTo(expectedResponse.getError());
    assertThat(actualResponse.getJSONArray("details"))
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(new JSONArray(expectedResponse.getDetails().toString()));
  }

  public static SignInDTO signIn(MockMvc mockMvc, ObjectMapper objectMapper, String username)
      throws Exception {
    final var signInRequest = new SignInRequest(username, "qwerty123");
    final var response =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var signInDTO = objectMapper.readValue(response, SignInDTO.class);
    return SignInDTO.builder()
        .accessToken(JwtUtil.TOKEN_TYPE + signInDTO.accessToken())
        .refreshToken(JwtUtil.TOKEN_TYPE + signInDTO.refreshToken())
        .userDTO(signInDTO.userDTO())
        .build();
  }
}
