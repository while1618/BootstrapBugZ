package org.bootstrapbugz.api.user.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private boolean activated;
  private boolean nonLocked;
  private LocalDateTime createdAt;

  @JsonProperty("roles")
  private Set<RoleDTO> roleDTOs;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDTO userDTO = (UserDTO) o;
    return activated == userDTO.activated
        && nonLocked == userDTO.nonLocked
        && Objects.equal(id, userDTO.id)
        && Objects.equal(firstName, userDTO.firstName)
        && Objects.equal(lastName, userDTO.lastName)
        && Objects.equal(username, userDTO.username)
        && Objects.equal(email, userDTO.email)
        && Objects.equal(roleDTOs, userDTO.roleDTOs);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, firstName, lastName, username, email, activated, nonLocked, createdAt, roleDTOs);
  }
}
