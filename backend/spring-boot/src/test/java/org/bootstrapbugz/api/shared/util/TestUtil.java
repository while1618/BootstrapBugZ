package org.bootstrapbugz.api.shared.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.auth.payload.dto.SignInDTO;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class TestUtil {
  private TestUtil() {}

  public static User getAdminUser() {
    return User.builder()
        .id(1L)
        .firstName("Admin")
        .lastName("Admin")
        .username("admin")
        .email("admin@localhost")
        .activated(true)
        .roles(Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)))
        .build();
  }

  public static User getTestUser() {
    return User.builder()
        .id(2L)
        .firstName("Test")
        .lastName("Test")
        .username("test")
        .email("test@localhost")
        .activated(true)
        .roles(Set.of(new Role(RoleName.USER)))
        .build();
  }

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

  public static SignInDTO signIn(
      MockMvc mockMvc, ObjectMapper objectMapper, SignInRequest signInRequest) throws Exception {
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signInRequest)))
            .andExpect(status().isOk());
    return objectMapper.readValue(
        resultActions.andReturn().getResponse().getContentAsString(), SignInDTO.class);
  }
}
