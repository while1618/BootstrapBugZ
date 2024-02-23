package org.bootstrapbugz.backend.shared.payload.dto;

import java.util.List;

public record FindAllDTO<T>(List<T> data, long total, int page, int limit) {}
