package org.bootstrapbugz.api.admin.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.user.model.Role.RoleName;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UpdateRoleRequest {
  @NotEmpty(message = "{usernames.empty}")
  protected Set<@Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String> usernames;

  @NotEmpty(message = "{roles.empty}")
  private Set<RoleName> roleNames;
}