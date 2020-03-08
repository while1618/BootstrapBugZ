package com.app.webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @NotEmpty(message = "NotEmpty.street")
    @Size(min = 2, max = 40, message = "Size.street")
    @Pattern(regexp = "^[a-zA-Z0-9 ,.'-]+$", message = "Regex.street")
    private String street;

    @NotEmpty(message = "NotEmpty.city")
    @Size(min = 2, max = 20, message = "Size.city")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.city")
    private String city;

    @NotEmpty(message = "NotEmpty.country")
    @Size(min = 2, max = 20, message = "Size.country")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.country")
    private String country;

    @JsonBackReference
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Department> departments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
}
