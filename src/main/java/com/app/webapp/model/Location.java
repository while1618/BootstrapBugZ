package com.app.webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id.equals(location.id) &&
                street.equals(location.street) &&
                city.equals(location.city) &&
                country.equals(location.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, city, country);
    }
}
