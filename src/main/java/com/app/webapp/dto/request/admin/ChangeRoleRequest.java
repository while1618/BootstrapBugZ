package com.app.webapp.dto.request.admin;

import com.app.webapp.model.user.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class ChangeRoleRequest extends AdminRequest {
    @NotEmpty(message = "{roles.notEmpty}")
    private List<RoleName> roleNames;
}
