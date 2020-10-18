package com.app.bootstrapbugz.admin.dto.request;

import com.app.bootstrapbugz.user.model.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class ChangeRoleRequest extends AdminRequest {
    @NotEmpty(message = "{roles.notEmpty}")
    private List<RoleName> roleNames;

    public ChangeRoleRequest(List<String> usernames, List<RoleName> roleNames) {
        super(usernames);
        this.roleNames = roleNames;
    }
}
