package com.app.webapp.dto.model.user;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto extends RepresentationModel<RoleDto> {
    private String name;
}
