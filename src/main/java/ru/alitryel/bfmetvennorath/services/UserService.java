package ru.alitryel.bfmetvennorath.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean checkBanned(Principal principal) {
        User user = userRepository.findByUserName(principal.getName()).get();
        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You Are Banned!");
        }
        return true;
    }

    public String getWinRate(User user) {
        // Здесь реализуйте логику для расчета винрейта пользователя
        // Используйте ваши запросы, чтобы получить общее количество игр и побед
        // Рассчитайте винрейт и верните его в виде строки

        Integer totalGames = userRepository.getAllGamesCount(user.getUserName());
        if (totalGames == null || totalGames == 0) {
            return "N/A"; // Избегаем деления на 0 и обрабатываем случай, когда игр нет
        }

        Integer totalWins = userRepository.getAllWinGamesCount(user.getUserName());
        if (totalWins == null) {
            totalWins = 0; // Если totalWins == null, считаем, что у пользователя 0 побед
        }

        double winRate = (double) totalWins / totalGames * 100;
        return String.format("%.2f%%", winRate);
    }

    public boolean saveUser(User user, Model model) {
        boolean validation = true;
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "E-mail is taken! Enter different!");
            validation = false;
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            model.addAttribute("usernameError", "User name taken, choose another!");
            validation = false;
        }
        if (validation) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreated(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public User findUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        return foundUser;
    }

    public User findUserByName(String name) {
        Optional<User> user = userRepository.findByUserName(name);
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        return foundUser;
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        return foundUser;
    }
}