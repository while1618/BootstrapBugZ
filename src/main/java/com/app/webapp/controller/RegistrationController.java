package com.app.webapp.controller;

import com.app.webapp.model.User;
import com.app.webapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("registration/login");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @GetMapping("/sign-up")
    public ModelAndView showSignUpForm() {
        ModelAndView modelAndView = new ModelAndView("registration/sign_up");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/sign-up")
    public ModelAndView signUp(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (userService.emailExist(user.getEmail())) {
            bindingResult.rejectValue("email", "exist", "Exist.email");
        }
        if (userService.usernameExist(user.getUsername())) {
            bindingResult.rejectValue("username", "exist", "Exist.username");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration/sign_up");
        } else {
            userService.save(user);
            modelAndView.setViewName("redirect:/login");
        }
        return modelAndView;
    }
}
