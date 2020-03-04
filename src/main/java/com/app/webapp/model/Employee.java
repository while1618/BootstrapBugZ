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

    @NotEmpty(message = "Please enter a first name.")
    @Size(min = 2, max = 16, message = "First name must be between 2 and 16 characters.")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Please enter a valid first name.")
    private String firstName;

    @NotEmpty(message = "Please enter a last name.")
    @Size(min = 2, max = 16, message = "Last name must be between 2 and 16 characters.")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Please enter a valid last name.")
    private String lastName;

    @NotEmpty(message = "Please enter a email.")
    @Email(message = "Please enter valid a email.")
    private String email;

    @NotEmpty(message = "Please enter a phone number.")
    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "Please enter a valid phone number.")
    private String phoneNumber;

    @NotNull(message = "Please enter a salary.")
    @Positive(message = "Salary must be positive number.")
    private Double salary;

    @NotNull(message = "Please enter a hire date.")
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
