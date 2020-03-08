package com.app.webapp.controller;

import com.app.webapp.model.Employee;
import com.app.webapp.service.DepartmentService;
import com.app.webapp.service.EmployeeService;
import com.app.webapp.validator.DepartmentValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class EmployeeController {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final DepartmentValidator departmentValidator;

    public EmployeeController(EmployeeService employeeService, DepartmentService departmentService, DepartmentValidator departmentValidator) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.departmentValidator = departmentValidator;
    }

    @GetMapping(value = "/employees")
    public ModelAndView employees(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        ModelAndView modelAndView = new ModelAndView("employee/employees");
        return modelAndView.addObject("employees", employeeService.findAllEmployees(page));
    }

    @GetMapping("/employee/create")
    public ModelAndView createEmployee() {
        ModelAndView modelAndView = new ModelAndView("employee/create");
        modelAndView.addObject("employee", new Employee());
        modelAndView.addObject("departments", departmentService.findAllDepartments());

        return modelAndView;
    }

    @PostMapping("/employee/create")
    public ModelAndView createEmployee(@Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        departmentValidator.validate(employee.getDepartment(), bindingResult);
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("departments", departmentService.findAllDepartments());
            modelAndView.setViewName("employee/create");
        } else {
            employeeService.createEmployee(employee);
            modelAndView.setViewName("redirect:/employees");
        }

        return modelAndView;
    }

    @GetMapping("/employee/edit/{id}")
    public String editEmployee(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("employee"))
            model.addAttribute("employee", employeeService.findEmployeeById(id));
        model.addAttribute("departments", departmentService.findAllDepartments());

        return "employee/edit";
    }

    @PostMapping("/employee/edit")
    public ModelAndView editEmployee(@Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        departmentValidator.validate(employee.getDepartment(), bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employee", bindingResult);
            redirectAttributes.addFlashAttribute("employee", employee);
            modelAndView.setViewName("redirect:/employee/edit/" + employee.getId());
        } else {
            employeeService.editEmployee(employee);
            modelAndView.setViewName("redirect:/employees");
        }

        return modelAndView;
    }

    @GetMapping("/employee/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
}
