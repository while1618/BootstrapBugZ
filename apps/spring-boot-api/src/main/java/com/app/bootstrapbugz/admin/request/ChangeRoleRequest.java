package com.app.bootstrapbugz.admin.request;

import com.app.bootstrapbugz.shared.constants.Regex;
import com.app.bootstrapbugz.user.model.RoleName;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ChangeRoleRequest {
  @NotEmpty(message = "{usernames.empty}")
  protected Set<@Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String> usernames;

  @NotEmpty(message = "{roles.empty}")
  private Set<RoleName> roleNames;
}
