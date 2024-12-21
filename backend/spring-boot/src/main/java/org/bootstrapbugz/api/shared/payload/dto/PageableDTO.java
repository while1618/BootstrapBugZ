package org.bootstrapbugz.api.shared.payload.dto;

import java.util.List;

public record PageableDTO<T>(List<T> data, long total) {}
