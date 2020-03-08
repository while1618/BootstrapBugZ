package com.app.webapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @NotEmpty(message = "NotEmpty.firstName")
    @Size(min = 2, max = 16, message = "Size.firstName")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.firstName")
    private String firstName;

    @NotEmpty(message = "NotEmpty.lastName")
    @Size(min = 2, max = 16, message = "Size.lastName")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.lastName")
    private String lastName;

    @NotEmpty(message = "NotEmpty.email")
    @Email(message = "Regex.email")
    private String email;

    @NotEmpty(message = "NotEmpty.phoneNumber")
    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Regex.phoneNumber")
    private String phoneNumber;

    @NotNull(message = "NotNull.salary")
    @Positive(message = "Positive.salary")
    private Double salary;

    @NotNull(message = "NotNull.localDate")
    private LocalDate hireDate;

    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getHireDate() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            return hireDate.format(formatter);
        } catch (Exception e) {
            return "";
        }
    }

    public void setHireDate(String hireDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            this.hireDate = LocalDate.parse(hireDate, formatter);
        } catch (DateTimeParseException e) {
            this.hireDate = null;
        }
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
