package com.app.webapp.controller;

import com.app.webapp.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegistrationController {
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("registration/login");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @GetMapping("/sign-up")
    public ModelAndView signUp() {
        ModelAndView modelAndView = new ModelAndView("registration/sign_up");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }
}
