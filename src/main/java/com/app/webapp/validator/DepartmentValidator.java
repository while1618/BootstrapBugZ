package com.app.webapp.validator;

import com.app.webapp.model.Department;
import com.app.webapp.model.Employee;
import com.app.webapp.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class DepartmentValidator implements ConstraintValidator<ValidDepartment, Employee> {
    @Autowired
    private IDepartmentService departmentService;

    @Override
    public void initialize(ValidDepartment constraintAnnotation) {

    }

    @Override
    public boolean isValid(Employee employee, ConstraintValidatorContext constraintValidatorContext) {
        List<Department> departments = departmentService.findAll();
        return departments.contains(employee.getDepartment());
    }
}
