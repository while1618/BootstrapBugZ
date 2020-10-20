package com.app.bootstrapbugz.admin.dto.request;

import com.app.bootstrapbugz.user.model.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class ChangeRoleRequest extends AdminRequest {
    @NotEmpty(message = "{roles.notEmpty}")
    private Set<RoleName> roleNames;

    public ChangeRoleRequest(Set<String> usernames, Set<RoleName> roleNames) {
        super(usernames);
        this.roleNames = roleNames;
    }
}
