package org.bootstrapbugz.api.shared.util;

import java.time.LocalDateTime;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;

public class UnitTestUtil {
  private UnitTestUtil() {}

  public static User getAdminUser() {
    return new User(
        1L,
        "Admin",
        "Admin",
        "admin",
        "admin@localhost",
        "",
        true,
        false,
        LocalDateTime.now(),
        Set.of(new Role(RoleName.USER), new Role(RoleName.ADMIN)));
  }

  public static User getTestUser() {
    return new User(
        2L,
        "Test",
        "Test",
        "test",
        "test@localhost",
        "",
        true,
        false,
        LocalDateTime.now(),
        Set.of(new Role(RoleName.USER)));
  }
}
