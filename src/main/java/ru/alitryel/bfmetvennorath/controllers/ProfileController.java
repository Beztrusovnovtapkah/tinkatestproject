package ru.alitryel.bfmetvennorath.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.alitryel.bfmetvennorath.entities.Replay;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.*;
import ru.alitryel.bfmetvennorath.services.RoomService;
import ru.alitryel.bfmetvennorath.entities.Role;
import ru.alitryel.bfmetvennorath.services.SearchService;
import ru.alitryel.bfmetvennorath.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ReplayRepository replayRepository;

    @GetMapping("/{userName}")
    public String userProfile(@PathVariable String userName, Model model) {
        Optional<User> user = userRepository.findByUserName(userName);
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        List<String> role = foundUser.getRoles().stream().map(Role::getName).toList();
        List<Replay> latestReplays = replayRepository.findTop10ByUsername(foundUser.getUserName(), PageRequest.of(0, 10));

        model.addAttribute("allCount", userRepository.getAllGamesCount(userName));
        model.addAttribute("allWin", userRepository.getAllWinGamesCount(userName));
        model.addAttribute("allLose", userRepository.getAllLoseGamesCount(userName));
        model.addAttribute("user", foundUser);
        model.addAttribute("role", role.get(0));
        model.addAttribute("latestReplays", latestReplays);

        return "/profile/profile";
    }


    @GetMapping()
    public String profilePage(Model model, Principal principal) {
        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        List<String> role = foundUser.getRoles().stream().map(Role::getName)
                .toList();
        List<Replay> latestReplays = replayRepository.findTop10ByUsername(foundUser.getUserName(), PageRequest.of(0, 10));
        model.addAttribute("allCount", userRepository.getAllGamesCount(principal.getName()));
        model.addAttribute("allWin", userRepository.getAllWinGamesCount(principal.getName()));
        model.addAttribute("allLose", userRepository.getAllLoseGamesCount(principal.getName()));
        model.addAttribute("user", foundUser);
        model.addAttribute("role", role.get(0));
        model.addAttribute("latestReplays", latestReplays);

        return "/profile/profile";
    }

    @GetMapping("/changePassword")
    public String showChangePasswordForm(Principal principal, Model model) {
        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        model.addAttribute("user", foundUser);
        return "/profile/changePassword :: changePasswordFragment";
    }

    //Process change password.
    @PostMapping("/changePassword")
    public String processChangePassword(@RequestParam String password, Principal principal) {
        User foundUser = userRepository.findByUserName(principal.getName()).get();
        foundUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(foundUser);
        return "redirect:/profile";
    }

    @GetMapping("/changeEmail")
    public String showChangeEmailForm(Model model, Principal principal) {
        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        model.addAttribute("user", foundUser);
        return "/profile/changeEmail :: changeEmailFragment";
    }

    @PostMapping("/changeEmail")
    public String processChangeEmail(@RequestParam String email, Principal principal) {
        User user = userRepository.findByUserName(principal.getName()).get();
        user.setEmail(email);
        userRepository.save(user);
        return "redirect:/profile";
    }

    @GetMapping("/profile")
    public String deleteAccount(@RequestParam long id, HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        User foundUser = userService.findUserById(id);
        userRepository.delete(foundUser);
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/logout";
    }
}
