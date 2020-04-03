package com.app.webapp.model;

import com.app.webapp.validator.ValidLocation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department extends RepresentationModel<Department> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @NotEmpty(message = "{department.name.notEmpty}")
    @Size(min = 2, max = 40, message = "{department.name.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{department.name.regex}")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Employee> employees;

    @ManyToOne()
    @JoinColumn(name = "location_id")
    @ValidLocation(message = "{department.location.notValid}")
    private Location location;

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location);
    }
}
