package org.bootstrapbugz.api.admin.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bootstrapbugz.api.shared.constants.Regex;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminRequest {
  @NotEmpty(message = "{usernames.empty}")
  protected Set<@Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String> usernames;
}
