package ru.alitryel.bfmetvennorath.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.alitryel.bfmetvennorath.entities.Role;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.RoleRepository;
import ru.alitryel.bfmetvennorath.services.UserService;


import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final HttpServletRequest servletRequest;
    private final UserService userService;
    private final RoleRepository roleRepository;


    @GetMapping("/register")
    public String registerUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String validateUsers(@Valid User user, BindingResult bindingResult, Model model) {
        user.setActive(true);
        user.setUserIP(servletRequest.getRemoteAddr());
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("USER").get());
        user.setRoles(roles);
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userService.saveUser(user, model)) {
            return "login";
        } else {
            return "register";
        }
    }
}