package ru.alitryel.bfmetvennorath.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;
import ru.alitryel.bfmetvennorath.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/rating")
public class RatingController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RatingController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping()
    public String topPlayers(Model model) {
        List<User> allPlayers = userRepository.findAllByOrderByEloDesc();

        // Добавляем винрейт для каждого игрока в модель
        for (User player : allPlayers) {
            String winRate = userService.getWinRate(player);
            model.addAttribute("winRate_" + player.getId(), winRate);
        }

        model.addAttribute("players", allPlayers);
        return "topPlayers";
    }
}
