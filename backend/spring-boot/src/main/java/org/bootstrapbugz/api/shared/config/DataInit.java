package org.bootstrapbugz.api.shared.config;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile({"dev", "test"})
@Component
public class DataInit implements ApplicationRunner {
  private static final String PASSWORD = "qwerty123";
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final Environment environment;
  private final Role userRole = new Role(RoleName.USER);
  private final Role adminRole = new Role(RoleName.ADMIN);

  public DataInit(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder bCryptPasswordEncoder,
      Environment environment) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.environment = environment;
  }

  @Override
  public void run(ApplicationArguments args) {
    roleRepository.saveAll(List.of(userRole, adminRole));
    if (environment.getActiveProfiles()[0].equals("dev")) devUsers();
    else if (environment.getActiveProfiles()[0].equals("test")) testUsers();
  }

  private void devUsers() {
    final var users =
        List.of(
            User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .username("admin")
                .email("admin@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Set.of(userRole, adminRole))
                .build(),
            User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .email("john.doe@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .username("jane.doe")
                .email("jane.doe@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build());
    userRepository.saveAll(users);
  }

  private void testUsers() {
    final var users =
        List.of(
            User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .username("admin")
                .email("admin@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Set.of(userRole, adminRole))
                .build(),
            User.builder()
                .firstName("User")
                .lastName("User")
                .username("user")
                .email("user@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("Not Activated")
                .lastName("Not Activated")
                .username("not.activated")
                .email("not.activated@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(false)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("Locked")
                .lastName("Locked")
                .username("locked")
                .email("locked@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(false)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("For Update 1")
                .lastName("For Update 1")
                .username("for.update.1")
                .email("for.update.1@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("For Update 2")
                .lastName("For Update 2")
                .username("for.update.2")
                .email("for.update.2@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build(),
            User.builder()
                .firstName("For Update 3")
                .lastName("For Update 3")
                .username("for.update.3")
                .email("for.update.3@bootstrapbugz.com")
                .password(bCryptPasswordEncoder.encode(PASSWORD))
                .activated(true)
                .nonLocked(true)
                .roles(Collections.singleton(userRole))
                .build());
    userRepository.saveAll(users);
  }
}
