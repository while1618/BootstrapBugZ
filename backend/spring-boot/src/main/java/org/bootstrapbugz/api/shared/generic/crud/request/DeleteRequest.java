package org.bootstrapbugz.api.shared.generic.crud.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRequest {
  @NotEmpty(message = "{ids.empty}")
  private Set<Long> ids;
}
