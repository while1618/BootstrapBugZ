package com.app.bootstrapbugz.dto.request.admin;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class AdminRequest {
    @NotEmpty(message = "{usernames.notEmpty}")
    private List<
            @NotEmpty(message = "{username.notEmpty}")
            @Size(min = 2, max = 16, message = "{username.size}")
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{username.regex}") String> usernames;
}
