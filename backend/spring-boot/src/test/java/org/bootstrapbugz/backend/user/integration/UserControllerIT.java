package org.bootstrapbugz.backend.user.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.bootstrapbugz.backend.shared.config.DatabaseContainers;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.backend.user.payload.request.UsernameAvailabilityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class UserControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private final UserDTO expectedUserDTO =
      UserDTO.builder()
          .id(2L)
          .firstName("User")
          .lastName("User")
          .username("user")
          .email(null)
          .createdAt(LocalDateTime.now())
          .build();

  @Test
  void findAllUsers() throws Exception {
    mockMvc
        .perform(get(Path.USERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(10))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.limit").value(10));
  }

  @Test
  void findUserById() throws Exception {
    final var response =
        mockMvc
            .perform(get(Path.USERS + "/{id}", 2L).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var actualUserDTO = objectMapper.readValue(response, UserDTO.class);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserById_throwResourceNotFound() throws Exception {
    mockMvc
        .perform(get(Path.USERS + "/{id}", 100L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("User not found.")));
  }

  @Test
  void findUserByUsername() throws Exception {
    final var response =
        mockMvc
            .perform(
                get(Path.USERS + "/username/{username}", "user")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var actualUserDTO = objectMapper.readValue(response, UserDTO.class);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsername_throwResourceNotFound() throws Exception {
    mockMvc
        .perform(
            get(Path.USERS + "/username/{username}", "unknown")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("User not found.")));
  }

  @Test
  void usernameAvailability() throws Exception {
    final var availabilityRequest = new UsernameAvailabilityRequest("user");
    mockMvc
        .perform(
            post(Path.USERS + "/username/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availabilityRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(false));
  }

  @Test
  void emailAvailability() throws Exception {
    final var availabilityRequest = new EmailAvailabilityRequest("unknown@localhost");
    mockMvc
        .perform(
            post(Path.USERS + "/email/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availabilityRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(true));
  }
}
