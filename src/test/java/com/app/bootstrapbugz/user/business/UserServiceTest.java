package com.app.bootstrapbugz.user.business;

import com.app.bootstrapbugz.user.dto.model.UserDto;
import com.app.bootstrapbugz.user.dto.request.ChangePasswordRequest;
import com.app.bootstrapbugz.user.dto.request.EditUserRequest;
import com.app.bootstrapbugz.error.exception.BadRequestException;
import com.app.bootstrapbugz.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.user.dto.hal.UserDtoModelAssembler;
import com.app.bootstrapbugz.user.model.Role;
import com.app.bootstrapbugz.user.model.RoleName;
import com.app.bootstrapbugz.user.model.User;
import com.app.bootstrapbugz.user.repository.UserRepository;
import com.app.bootstrapbugz.jwt.util.JwtUtilities;
import com.app.bootstrapbugz.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageSource messageSource;
    @Spy
    private UserDtoModelAssembler assembler;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private JwtUtilities jwtUtilities;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Role userRole;
    private User user;

    @BeforeEach
    void init() {
        userRole = new Role(1L, RoleName.ROLE_USER);
        user = new User()
                .setId(2L)
                .setFirstName("User")
                .setLastName("User")
                .setUsername("user")
                .setEmail("user@localhost.com")
                .setPassword(bCryptPasswordEncoder.encode("123"))
                .setActivated(true)
                .setRoles(Set.of(userRole));
    }

    private void authentication() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void authentication_withUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user");
    }

    @Test
    void findAll_ok() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        CollectionModel<UserDto> foundUsers = userService.findAll();
        assertThat(foundUsers)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void findAll_notFound() {
        assertThrows(ResourceNotFound.class, () -> userService.findAll());
    }

    @Test
    void findByUsername_ok() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        UserDto foundUser = userService.findByUsername("user");
        assertThat(foundUser).isNotNull();
        assertEquals(foundUser.getUsername(), user.getUsername());
    }

    @Test
    void findByUsername_notFound() {
        assertThrows(ResourceNotFound.class, () -> userService.findByUsername("user"));
    }

    @Test
    void editUser_ok() {
        authentication_withUser();
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "test@localhost.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByUsername(editUserRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(editUserRequest.getEmail())).thenReturn(false);
        User expectedUser = expectedEditedUser(editUserRequest);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        UserDto returnedUser = userService.edit(editUserRequest);
        assertThat(returnedUser).isNotNull();
        assertEquals(returnedUser.getUsername(), user.getUsername());
        assertEquals(returnedUser.getEmail(), user.getEmail());
        assertEquals(returnedUser.isActivated(), user.isActivated());
    }

    private User expectedEditedUser(EditUserRequest editUserRequest) {
        return new User()
                .setId(2L)
                .setFirstName(editUserRequest.getFirstName())
                .setLastName(editUserRequest.getLastName())
                .setUsername(editUserRequest.getUsername())
                .setEmail(editUserRequest.getEmail())
                .setPassword(bCryptPasswordEncoder.encode("123"))
                .setActivated(false)
                .setRoles(Set.of(userRole));
    }

    @Test
    void editUser_noAuth_userNotFound() {
        authentication();
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "test@localhost.com");
        assertThrows(ResourceNotFound.class, () -> userService.edit(editUserRequest));
    }

    @Test
    void editUser_usernameExists_badRequest() {
        authentication_withUser();
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "admin", "test@localhost.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByUsername(editUserRequest.getUsername())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.edit(editUserRequest));
    }

    @Test
    void editUser_emailExists_badRequest() {
        authentication_withUser();
        EditUserRequest editUserRequest = new EditUserRequest("Test", "Test", "test", "admin@localhost.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmail(editUserRequest.getEmail())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.edit(editUserRequest));
    }

    @Test
    void editUser_noChanges() {
        authentication_withUser();
        EditUserRequest editUserRequest = new EditUserRequest("User", "User", "user", "user@localhost.com");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto noChangesUser = userService.edit(editUserRequest);
        assertThat(noChangesUser).isNotNull();
        assertEquals(noChangesUser.getUsername(), user.getUsername());
        assertEquals(noChangesUser.getEmail(), user.getEmail());
    }

    @Test
    void changePassword_ok() {
        authentication_withUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        userService.changePassword(changePasswordRequest);
        assertFalse(bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()));
        assertTrue(bCryptPasswordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword()));
    }

    @Test
    void changePassword_noAuth_userNotFound() {
        authentication();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("123", "1234", "1234");
        assertThrows(ResourceNotFound.class, () -> userService.changePassword(changePasswordRequest));
    }

    @Test
    void changePassword_badRequest() {
        authentication_withUser();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("12345", "1234", "1234");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        assertThrows(BadRequestException.class, () -> userService.changePassword(changePasswordRequest));
    }

    @Test
    void logoutFromAllDevices_ok() {
        authentication_withUser();
        LocalDateTime beforeLogout = user.getLogoutFromAllDevicesAt();
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        userService.logoutFromAllDevices();
        assertNotEquals(beforeLogout, user.getLogoutFromAllDevicesAt());
    }

    @Test
    void logoutFromAllDevices_noAuth_userNotFound() {
        authentication();
        assertThrows(ResourceNotFound.class, () -> userService.logoutFromAllDevices());
    }
}
