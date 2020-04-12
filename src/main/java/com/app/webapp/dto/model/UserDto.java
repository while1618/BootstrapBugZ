package com.app.webapp.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDto extends RepresentationModel<UserDto> {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private boolean activated;
    private List<RoleDto> roles;
}
