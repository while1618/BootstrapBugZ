package org.bootstrapbugz.api.user.payload.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoleDto {
  private Long id;
  private String name;

  public RoleDto(String name) {
    this.name = name;
  }
}
