package org.bootstrapbugz.api.user.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class UserResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private boolean activated;
  private boolean nonLocked;
  private Set<RoleDto> roles;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static final class RoleDto {
    private String name;
  }
}
