package com.app.bootstrapbugz.user.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleUserDto {
  private String firstName;
  private String lastName;
  private String username;
  private String email;
}
