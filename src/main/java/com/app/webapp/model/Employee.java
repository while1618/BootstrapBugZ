package com.app.webapp.model;

import com.app.webapp.validator.ValidDepartment;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "employees")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @NotEmpty(message = "{firstName.notEmpty}")
    @Size(min = 2, max = 16, message = "{firstName.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{firstName.regex}")
    private String firstName;

    @NotEmpty(message = "{lastName.notEmpty}")
    @Size(min = 2, max = 16, message = "{lastName.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{lastName.regex}")
    private String lastName;

    @NotEmpty(message = "{email.notEmpty}")
    @Email(message = "{email.regex}")
    private String email;

    @NotEmpty(message = "{phoneNumber.notEmpty}")
    @Pattern(regexp = "^[+]*[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$", message = "{phoneNumber.regex}")
    private String phoneNumber;

    @NotNull(message = "{employee.salary.notNull}")
    @Positive(message = "{employee.salary.positive}")
    private Double salary;

    @NotNull(message = "{employee.hireDate.notNull}")
    private LocalDate hireDate;

    @ManyToOne()
    @JoinColumn(name = "department_id")
    @ValidDepartment(message = "{employee.department.notValid}")
    private Department department;

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
}
