package org.bootstrapbugz.api.user.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(exclude = {"createdAt"})
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
}
