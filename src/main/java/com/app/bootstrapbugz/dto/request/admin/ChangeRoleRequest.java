package com.app.bootstrapbugz.dto.request.admin;

import com.app.bootstrapbugz.model.user.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest extends AdminRequest {
    @NotEmpty(message = "{roles.notEmpty}")
    private List<RoleName> roleNames;
}
