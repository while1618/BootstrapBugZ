package com.app.bootstrapbugz.shared.generic.crud.request;

import java.util.Set;
import javax.validation.constraints.NotEmpty;
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
