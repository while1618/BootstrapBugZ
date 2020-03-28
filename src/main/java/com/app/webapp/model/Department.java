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
@Table(name = "departments")
public class Department {
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
    private Location location;
}
