package org.bootstrapbugz.api.shared.config;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import jakarta.annotation.PostConstruct;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Profile({"dev", "test"})
@Component
public class DataInit {
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

  @PostConstruct
  public void init() {
    final var roles = List.of(userRole, adminRole);
    roleRepository.saveAll(roles);
    if (environment.getActiveProfiles()[0].equals("dev")) devUsers();
    else if (environment.getActiveProfiles()[0].equals("test")) testUsers();
  }

  private void devUsers() {
    final var users =
        List.of(
            new User()
                .setFirstName("Admin")
                .setLastName("Admin")
                .setUsername("admin")
                .setEmail("admin@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setRoles(Set.of(userRole, adminRole)),
            new User()
                .setFirstName("John")
                .setLastName("Doe")
                .setUsername("john.doe")
                .setEmail("john.doe@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("Jane")
                .setLastName("Doe")
                .setUsername("jane.doe")
                .setEmail("jane.doe@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setRoles(Collections.singleton(userRole)));
    userRepository.saveAll(users);
  }

  private void testUsers() {
    final var users =
        List.of(
            new User()
                .setFirstName("Admin")
                .setLastName("Admin")
                .setUsername("admin")
                .setEmail("admin@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(true)
                .setRoles(Set.of(userRole, adminRole)),
            new User()
                .setFirstName("User")
                .setLastName("User")
                .setUsername("user")
                .setEmail("user@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(true)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("Not Activated")
                .setLastName("Not Activated")
                .setUsername("not.activated")
                .setEmail("not.activated@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(false)
                .setNonLocked(true)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("Locked")
                .setLastName("Locked")
                .setUsername("locked")
                .setEmail("locked@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(false)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("For Update 1")
                .setLastName("For Update 1")
                .setUsername("for.update.1")
                .setEmail("for.update.1@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(true)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("For Update 2")
                .setLastName("For Update 2")
                .setUsername("for.update.2")
                .setEmail("for.update.2@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(true)
                .setRoles(Collections.singleton(userRole)),
            new User()
                .setFirstName("For Update 3")
                .setLastName("For Update 3")
                .setUsername("for.update.3")
                .setEmail("for.update.3@bootstrapbugz.com")
                .setPassword(bCryptPasswordEncoder.encode(PASSWORD))
                .setActivated(true)
                .setNonLocked(true)
                .setRoles(Collections.singleton(userRole)));
    userRepository.saveAll(users);
  }
}
