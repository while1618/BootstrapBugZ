package org.bootstrapbugz.api.user.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  //  @Mock private UserRepository userRepository;
  //  @Mock private MessageService messageService;
  //  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  //  @Mock private AccessTokenService accessTokenService;
  //  @Mock private RefreshTokenService refreshTokenService;
  //  @Mock private VerificationTokenService verificationTokenService;
  //  @InjectMocks private ProfileServiceImpl profileService;
  //  @Mock private Authentication auth;
  //  @Mock private SecurityContext securityContext;
  //  @Captor private ArgumentCaptor<User> userArgumentCaptor;
  //
  //  @BeforeEach
  //  void setUp() {
  //    when(securityContext.getAuthentication()).thenReturn(auth);
  //    SecurityContextHolder.setContext(securityContext);
  //    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
  //  }
  //
  //  @Test
  //  void updateUser_newUsernameAndEmail() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("User")
  //            .lastName("User")
  //            .username("user")
  //            .email("user@localhost")
  //            .activated(false)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //    final var updateUserRequest =
  //        new UpdateProfileRequest("User", "User", "user", "user@localhost");
  //    when(userRepository.findByUsernameWithRoles("test"))
  //        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    when(userRepository.existsByUsername(updateUserRequest.username())).thenReturn(false);
  //    when(userRepository.existsByEmail(updateUserRequest.email())).thenReturn(false);
  //    profileService.update(updateUserRequest);
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void updateUser_sameUsernameAndEmail() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("User")
  //            .lastName("User")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(true)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //    final var updateUserRequest =
  //        new UpdateProfileRequest("User", "User", "test", "test@localhost");
  //    when(userRepository.findByUsernameWithRoles("test"))
  //        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    profileService.update(updateUserRequest);
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void updateUser_throwBadRequest_usernameExists() {
  //    final var updateUserRequest =
  //        new UpdateProfileRequest("Test", "Test", "admin", "test@localhost");
  //    when(userRepository.findByUsernameWithRoles("test"))
  //        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    when(userRepository.existsByUsername(updateUserRequest.username())).thenReturn(true);
  //    when(messageService.getMessage("username.exists")).thenReturn("Username already exists.");
  //    assertThatThrownBy(() -> profileService.update(updateUserRequest))
  //        .isInstanceOf(ConflictException.class)
  //        .hasMessage("Username already exists.");
  //  }
  //
  //  @Test
  //  void updateUser_throwBadRequest_emailExists() {
  //    final var updateUserRequest =
  //        new UpdateProfileRequest("Test", "Test", "test", "admin@localhost");
  //    when(userRepository.findByUsernameWithRoles("test"))
  //        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    when(userRepository.existsByEmail(updateUserRequest.email())).thenReturn(true);
  //    when(messageService.getMessage("email.exists")).thenReturn("Email already exists.");
  //    assertThatThrownBy(() -> profileService.update(updateUserRequest))
  //        .isInstanceOf(ConflictException.class)
  //        .hasMessage("Email already exists.");
  //  }
  //
  //  @Test
  //  void changePassword() {
  //    final var changePasswordRequest =
  //        new ChangePasswordRequest("qwerty123", "qwerty1234", "qwerty1234");
  //    final var testUser = UnitTestUtil.getTestUser();
  //    testUser.setPassword(bCryptPasswordEncoder.encode("qwerty123"));
  //    when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
  //    profileService.changePassword(changePasswordRequest);
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(
  //            bCryptPasswordEncoder.matches(
  //                changePasswordRequest.newPassword(),
  // userArgumentCaptor.getValue().getPassword()))
  //        .isTrue();
  //  }
  //
  //  @Test
  //  void changePassword_thrownBadRequest_wrongOldPassword() {
  //    when(messageService.getMessage("oldPassword.invalid")).thenReturn("Wrong old password.");
  //    final var changePasswordRequest =
  //        new ChangePasswordRequest("qwerty123456", "qwerty1234", "qwerty1234");
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    assertThatThrownBy(() -> profileService.changePassword(changePasswordRequest))
  //        .isInstanceOf(BadRequestException.class)
  //        .hasMessage("Wrong old password.");
  //  }
  //
  //  @Test
  //  void delete() {
  //    profileService.delete();
  //    verify(userRepository, times(1)).deleteById(any(Long.class));
  //  }
}
