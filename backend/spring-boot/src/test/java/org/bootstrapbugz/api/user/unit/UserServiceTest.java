package org.bootstrapbugz.api.user.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  //  @Mock private UserRepository userRepository;
  //  @Mock private MessageService messageService;
  //  @InjectMocks private UserServiceImpl userService;
  //  @Mock private Authentication auth;
  //  @Mock private SecurityContext securityContext;
  //
  //  @BeforeEach
  //  void setUp() {
  //    when(securityContext.getAuthentication()).thenReturn(auth);
  //    SecurityContextHolder.setContext(securityContext);
  //  }
  //
  //  @Test
  //  void findAllUsers_rolesAndEmailsHidden() {
  //    when(auth.getPrincipal()).thenReturn("anonymousUser");
  //    final var expectedUserDTOs =
  //        List.of(
  //            UserDTO.builder()
  //                .id(1L)
  //                .firstName("Admin")
  //                .lastName("Admin")
  //                .username("admin")
  //                .activated(true)
  //                .nonLocked(true)
  //                .build(),
  //            UserDTO.builder()
  //                .id(2L)
  //                .firstName("Test")
  //                .lastName("Test")
  //                .username("test")
  //                .activated(true)
  //                .nonLocked(true)
  //                .build());
  //    when(userRepository.findAll())
  //        .thenReturn(List.of(UnitTestUtil.getAdminUser(), UnitTestUtil.getTestUser()));
  //    final var actualUserDTOs = userService.findAll();
  //    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  //  }
  //
  //  @Test
  //  void findAllUsers_rolesAndEmailsShown() {
  //    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getAdminUser()));
  //    final var expectedUserDTOs =
  //        List.of(
  //            UserDTO.builder()
  //                .id(1L)
  //                .firstName("Admin")
  //                .lastName("Admin")
  //                .username("admin")
  //                .email("admin@localhost")
  //                .activated(true)
  //                .nonLocked(true)
  //                .roleDTOs(
  //                    Set.of(new RoleDTO(RoleName.USER.name()), new
  // RoleDTO(RoleName.ADMIN.name())))
  //                .build(),
  //            UserDTO.builder()
  //                .id(2L)
  //                .firstName("Test")
  //                .lastName("Test")
  //                .username("test")
  //                .email("test@localhost")
  //                .activated(true)
  //                .nonLocked(true)
  //                .roleDTOs(Set.of(new RoleDTO(RoleName.USER.name())))
  //                .build());
  //    when(userRepository.findAllWithRoles())
  //        .thenReturn(List.of(UnitTestUtil.getAdminUser(), UnitTestUtil.getTestUser()));
  //    final var actualUserDTOs = userService.findAll();
  //    assertThat(actualUserDTOs).isEqualTo(expectedUserDTOs);
  //  }
  //
  //  @Test
  //  void findUserByUsername_emailHidden() {
  //    when(auth.getPrincipal()).thenReturn("anonymousUser");
  //    final var expectedUserDTO =
  //        UserDTO.builder()
  //            .id(1L)
  //            .firstName("Admin")
  //            .lastName("Admin")
  //            .username("admin")
  //            .nonLocked(true)
  //            .activated(true)
  //            .build();
  //    when(userRepository.findByUsername("admin"))
  //        .thenReturn(Optional.of(UnitTestUtil.getAdminUser()));
  //    final var actualUserDTO = userService.findByUsername("admin");
  //    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  //  }
  //
  //  @Test
  //  void findUserByUsername_emailShown() {
  //    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getTestUser()));
  //    final var expectedUserDTO =
  //        UserDTO.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .nonLocked(true)
  //            .activated(true)
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    final var actualUserDTO = userService.findByUsername("test");
  //    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  //  }
  //
  //  @Test
  //  void findUserByUsername_adminSignedIn() {
  //    when(auth.getPrincipal()).thenReturn(UserPrincipal.create(UnitTestUtil.getAdminUser()));
  //    final var expectedUserDTO =
  //        UserDTO.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .nonLocked(true)
  //            .activated(true)
  //            .roleDTOs(Set.of(new RoleDTO(RoleName.USER.name())))
  //            .build();
  //    when(userRepository.findByUsernameWithRoles("test"))
  //        .thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    final var actualUserDTO = userService.findByUsername("test");
  //    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  //  }
  //
  //  @Test
  //  void findUserByUsername_throwResourceNotFound() {
  //    when(auth.getPrincipal()).thenReturn("anonymousUser");
  //    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
  //    assertThatThrownBy(() -> userService.findByUsername("test"))
  //        .isInstanceOf(ResourceNotFoundException.class)
  //        .hasMessage("User not found.");
  //  }
}
