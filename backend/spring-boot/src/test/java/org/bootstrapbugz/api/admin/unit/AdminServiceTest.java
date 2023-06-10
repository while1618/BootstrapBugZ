package org.bootstrapbugz.api.admin.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  //  @Mock private UserRepository userRepository;
  //  @Mock private RoleRepository roleRepository;
  //  @Mock private AccessTokenService accessTokenService;
  //  @Mock private RefreshTokenService refreshTokenService;
  //  @Mock private MessageService messageService;
  //  @InjectMocks private AdminServiceImpl adminService;
  //  @Captor private ArgumentCaptor<User> userArgumentCaptor;
  //
  //  @Test
  //  void updateUserRoles() {
  //    final var updateRolesRequest = new UpdateRoleRequest(Set.of(RoleName.USER, RoleName.ADMIN));
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(true)
  //            .roles(Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)))
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    when(roleRepository.findAllByNameIn(updateRolesRequest.roleNames()))
  //        .thenReturn(List.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)));
  //    adminService.updateRole("test", updateRolesRequest);
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void lockUser() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(true)
  //            .nonLocked(false)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    adminService.lock("test");
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void unlockUser() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(true)
  //            .nonLocked(true)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    adminService.unlock("test");
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void activateUser() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(true)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    adminService.activate("test");
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void deactivateUser() {
  //    final var expectedUser =
  //        User.builder()
  //            .id(2L)
  //            .firstName("Test")
  //            .lastName("Test")
  //            .username("test")
  //            .email("test@localhost")
  //            .activated(false)
  //            .roles(Set.of(new Role(RoleName.USER)))
  //            .build();
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    adminService.deactivate("test");
  //    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
  //    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  //  }
  //
  //  @Test
  //  void deleteUser() {
  //
  // when(userRepository.findByUsername("test")).thenReturn(Optional.of(UnitTestUtil.getTestUser()));
  //    adminService.delete("test");
  //    verify(userRepository, times(1)).delete(any(User.class));
  //  }
}
