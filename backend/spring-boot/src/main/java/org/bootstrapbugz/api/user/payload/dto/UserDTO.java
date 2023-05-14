package org.bootstrapbugz.api.user.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserDTO(
    Long id,
    String firstName,
    String lastName,
    String username,
    String email,
    boolean activated,
    boolean nonLocked,
    LocalDateTime createdAt,
    @JsonProperty("roles") Set<RoleDTO> roleDTOs) {
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
        id, firstName, lastName, username, email, activated, nonLocked, roleDTOs);
  }
}
