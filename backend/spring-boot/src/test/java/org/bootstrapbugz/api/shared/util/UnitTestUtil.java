package org.bootstrapbugz.api.shared.util;

import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;

public class UnitTestUtil {
  private UnitTestUtil() {}

  public static User getAdminUser() {
    return User.builder()
        .id(1L)
        .firstName("Admin")
        .lastName("Admin")
        .username("admin")
        .email("admin@localhost")
        .active(true)
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
        .active(true)
        .roles(Set.of(new Role(RoleName.USER)))
        .build();
  }
}
