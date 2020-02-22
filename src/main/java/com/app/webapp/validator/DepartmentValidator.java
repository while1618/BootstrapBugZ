package com.app.webapp.validator;

import com.app.webapp.model.Department;
import com.app.webapp.service.DepartmentServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class DepartmentValidator implements Validator {
    private final DepartmentServiceImpl departmentService;

    public DepartmentValidator(DepartmentServiceImpl departmentService) {
        this.departmentService = departmentService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Department.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Department department = (Department) o;
        List<Department> departments = departmentService.findAllDepartments();
        if (!departments.contains(department))
            errors.rejectValue("department", "employee.department","Please select valid department.");
    }
}
