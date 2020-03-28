package com.app.webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @NotEmpty(message = "{location.street.notEmpty}")
    @Size(min = 2, max = 40, message = "{location.street.size}")
    @Pattern(regexp = "^[a-zA-Z0-9 ,.'-]+$", message = "{location.street.regex}")
    private String street;

    @NotEmpty(message = "{location.city.notEmpty}")
    @Size(min = 2, max = 20, message = "{location.city.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{location.city.regex}")
    private String city;

    @NotEmpty(message = "{location.country.notEmpty}")
    @Size(min = 2, max = 20, message = "{location.country.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{location.country.regex}")
    private String country;

    @JsonBackReference
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Department> departments;
}
