package org.bootstrapbugz.api.user.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.UnitTestUtil;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private MessageService messageService;
  @InjectMocks private UserServiceImpl userService;

  @Test
  void findAllUsers() {
    final var expectedUserDTOs =
        List.of(
            UserDTO.builder()
                .id(1L)
                .firstName("Admin")
                .lastName("Admin")
                .username("admin")
                .email(null)
                .build(),
            UserDTO.builder()
                .id(2L)
                .firstName("Test")
                .lastName("Test")
                .username("test")
                .email(null)
                .build());
    when(userRepository.findAll(PageRequest.of(0, 10)))
        .thenReturn(
            new PageImpl<>(List.of(UnitTestUtil.getAdminUser(), UnitTestUtil.getTestUser())));
    final var actualUserDTOs = userService.findAll(PageRequest.of(0, 10));
    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  }

  @Test
  void findUserById() {
    final var expectedUserDTO =
        UserDTO.builder()
            .id(1L)
            .firstName("Admin")
            .lastName("Admin")
            .username("admin")
            .email(null)
            .build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(UnitTestUtil.getAdminUser()));
    final var actualUserDTO = userService.findById(1L);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserById_throwResourceNotFound() {
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findById(100L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void findUserByUsername() {
    final var expectedUserDTO =
        UserDTO.builder()
            .id(1L)
            .firstName("Admin")
            .lastName("Admin")
            .username("admin")
            .email(null)
            .build();
    when(userRepository.findByUsername("admin"))
        .thenReturn(Optional.of(UnitTestUtil.getAdminUser()));
    final var actualUserDTO = userService.findByUsername("admin");
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void findUserByUsername_throwResourceNotFound() {
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> userService.findByUsername("test"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void usernameAvailability() {
    when(userRepository.existsByUsername("admin")).thenReturn(true);
    final var availabilityRequest = new UsernameAvailabilityRequest("admin");
    final var availabilityDTO = userService.usernameAvailability(availabilityRequest);
    assertThat(availabilityDTO.available()).isFalse();
  }

  @Test
  void emailAvailability() {
    when(userRepository.existsByEmail("unknown@localhost")).thenReturn(false);
    final var availabilityRequest = new EmailAvailabilityRequest("unknown@localhost");
    final var availabilityDTO = userService.emailAvailability(availabilityRequest);
    assertThat(availabilityDTO.available()).isTrue();
  }
}
