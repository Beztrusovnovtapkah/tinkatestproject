package ru.alitryel.bfmetvennorath.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.RoomRepository;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;
import ru.alitryel.bfmetvennorath.services.RoomService;
import ru.alitryel.bfmetvennorath.entities.Role;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;

    @GetMapping("/")
    public String showHomePage(Model model, Principal principal) {
        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        List<String> role = foundUser.getRoles().stream().map(Role::getName)
                .toList();
        model.addAttribute("user", foundUser);
        model.addAttribute("role", role.get(0));
        roomService.cleanRooms(roomRepository.findAll());
        return "home";
    }
}
