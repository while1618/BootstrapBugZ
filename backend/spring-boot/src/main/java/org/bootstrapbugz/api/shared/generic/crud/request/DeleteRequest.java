package org.bootstrapbugz.api.shared.generic.crud.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record DeleteRequest(@NotEmpty(message = "{ids.empty}") Set<Long> ids) {}
