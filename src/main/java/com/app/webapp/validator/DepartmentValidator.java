package com.app.webapp.validator;

import com.app.webapp.model.Department;
import com.app.webapp.service.IDepartmentService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class DepartmentValidator implements ConstraintValidator<ValidDepartment, Department> {
    private final IDepartmentService departmentService;

    public DepartmentValidator(IDepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Override
    public void initialize(ValidDepartment constraintAnnotation) {

    }

    @Override
    public boolean isValid(Department department, ConstraintValidatorContext constraintValidatorContext) {
        List<Department> departments = departmentService.findAll();
        return departments.contains(department);
    }
}
