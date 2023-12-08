package ru.alitryel.bfmetvennorath.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.alitryel.bfmetvennorath.components.OutputMessage;
import ru.alitryel.bfmetvennorath.entities.*;
import ru.alitryel.bfmetvennorath.repositories.*;
import ru.alitryel.bfmetvennorath.services.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final SearchRepository searchRepository;
    private final UserService userService;
    private final PlayerReadyStatusService playerReadyStatusService;
    private final MessageService messageService;

    @GetMapping("/showRoom")
    public String showRoom(@RequestParam Long id, Model model, Authentication authentication, HttpSession session, Principal principal) {
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow();
        Message message = new Message();
        Room foundRoom = roomService.findRoomById(id);

        Map<Long, Boolean> playerReadyStatus = (Map<Long, Boolean>) session.getAttribute("playerReadyStatus");
        if (playerReadyStatus == null) {
            playerReadyStatus = new HashMap<>();
            session.setAttribute("playerReadyStatus", playerReadyStatus);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm");
        String formattedCreated = foundRoom.getCreated().format(formatter);
        String formattedExpires = foundRoom.getExpires().format(formatter);
        model.addAttribute("formattedCreated", formattedCreated);
        model.addAttribute("formattedExpires", formattedExpires);

        model.addAttribute("playerReadyStatus", playerReadyStatus);
        model.addAttribute("room", foundRoom);
        model.addAttribute("message", message);
        model.addAttribute("user", user);

        List<User> userList = foundRoom.getUserList();
        if (userList.stream().anyMatch(u -> u.getId() == (user.getId()))) {
            return "/room/singleRoomUserOnList";
        } else {
            return "/room/singleRoom";
        }
    }


    // http://localhost:8080/room/addMessage
    // add message to database
    @PostMapping("/addMessage")
    public String addMessage(Message message, @RequestParam Long roomId, Authentication authentication) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room does not exist"));
        message.setRoom(room);
        if (messageService.saveMessage(authentication, message)) {
            return "redirect:/room/showRoom?id=" + room.getId();
        } else {
            return "/room/wrongMessage";
        }
    }

    // http://localhost:8080/room/joinRoom
    //allows user to join room
    @GetMapping("/joinRoom")
    public String joinToRoom(@RequestParam long id, Authentication authentication, HttpSession session) {
        Room foundRoom = roomService.findRoomById(id);
        User foundUser = userService.findUserByName(authentication.getName());

        if (roomRepository.isUserInRoom(foundRoom.getId(), foundUser.getId())) {
            return "redirect:/room/rooms";
        }
        boolean check = true;
        for (User u : foundRoom.getUserList()) {
            if (u.getId() == foundUser.getId()) {
                check = false;
            }

            if (foundRoom.getMaxCount() <= foundRoom.getUserList().size()) {
                check = false;
            }
        }
        if (check) {
            foundRoom.getUserList().add(foundUser);
            playerReadyStatusService.setPlayerReadyStatus(foundUser.getId(), false);
            session.setAttribute("playerReadyStatus", playerReadyStatusService.getPlayerReadyStatus());
        }

        roomRepository.save(foundRoom);
        return "redirect:/room/rooms";
    }

    // http://localhost:8080/room/leaveRoom
    // allows user to leave room
    @GetMapping("/leaveRoom")
    public String leaveRoom(@RequestParam long id, Authentication authentication, HttpSession session) {
        Room foundRoom = roomService.findRoomById(id);
        User foundUser = userService.findUserByName(authentication.getName());
        Map<Long, Boolean> playerReadyStatus = (Map<Long, Boolean>) session.getAttribute("playerReadyStatus");
        List<User> userList = foundRoom.getUserList();
        if (userList.size() >= 2 && playerReadyStatus.get(userList.get(0).getId()) && playerReadyStatus.get(userList.get(1).getId())) {
            return "redirect:/room/rooms";
        }
        if (Objects.equals(foundRoom.getCapitanRoom(), userRepository.findByUserName(authentication.getName()).get().getUserName())) {
            roomRepository.delete(foundRoom);
            return "redirect:/room/rooms";
        }
        foundRoom.getUserList().removeIf(u -> u.getId() == foundUser.getId());
        roomRepository.save(foundRoom);

        return "redirect:/room/rooms";
    }

    @PostMapping("/deleteRoom")
    public String deleteRoom(@RequestParam Long roomId, Principal principal) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // Проверяем, что текущий пользователь - капитан комнаты
            if (Objects.equals(room.getCapitanRoom(), principal.getName())) {
                // Удаляем комнату
                roomRepository.delete(room);
            }
        }

        return "redirect:/room/rooms";
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(OutputMessage outputMessage) throws Exception {
        messageService.saveMessageFromWebSocket(outputMessage);
        return new OutputMessage(outputMessage.getText(), outputMessage.getSender(), outputMessage.getRoom());
    }
    @PostMapping("/toggleReady")
    public String toggleReady(@RequestParam long userId, @RequestParam long roomId, Authentication authentication, HttpSession session) {
        User user = userRepository.findByUserName(authentication.getName()).orElseThrow();
        if (user.getId() != userId) {
            return "redirect:/room/showRoom?id=" + roomId;
        }

        Map<Long, Boolean> playerReadyStatus = (Map<Long, Boolean>) session.getAttribute("playerReadyStatus");
        if (playerReadyStatus == null) {
            playerReadyStatus = new HashMap<>();
            session.setAttribute("playerReadyStatus", playerReadyStatus);
        }

        Boolean userReadyStatus = playerReadyStatus.get(userId);

        if (userReadyStatus != null) {
            playerReadyStatus.put(userId, !userReadyStatus);

            Room room = roomService.findRoomById(roomId);
            session.setAttribute("playerReadyStatus", playerReadyStatus);
            List<User> userList = room.getUserList();
            if (userList.size() >= 2 && playerReadyStatus.get(userList.get(0).getId()) && playerReadyStatus.get(userList.get(1).getId())) {
                // Оба игрока готовы
                room.setGameStarted(true);

                // Устанавливаем флаг загрузки реплея
                room.setReplayUploaded(true);

                // Сохраняем комнату
                roomRepository.save(room);

                messagingTemplate.convertAndSend("/topic/gameStarted", true);
            }

            return "redirect:/room/showRoom?id=" + roomId;
        } else {
            return "redirect:/room/showRoom?id=" + roomId;
        }
    }

    @GetMapping("/addRoom")
    public String showCreateRoomForm(Model model) {
        List<Game> gameList = gameRepository.findAll();
        Room room = new Room();
        model.addAttribute("room", room);
        model.addAttribute("gameList", gameList);
        return "/room/createRoomForm";
    }

    @PostMapping("/addRoom")
    public String processCreateRoomForm(String nameRoom, Authentication authentication, Room room, BindingResult bindingResult, Game game, HttpSession session) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "/room/createRoomForm";
        }

        User foundUser = userService.findUserByName(authentication.getName());

        if (roomRepository.existsByCapitanRoom(foundUser.getUserName())) {
            return "/room/roomExists";
        }

        List<User> listUser = new ArrayList<>();
        listUser.add(foundUser);
        room.setName(nameRoom);
        room.setCapitanRoom(authentication.getName());
        room.setUserList(listUser);
        room.setMaxCount(game.getMaxCount());
        room.setReplayUploaded(false);

        // Установка статуса готовности для создателя комнаты
        playerReadyStatusService.setPlayerReadyStatus(foundUser.getId(), false);
        session.setAttribute("playerReadyStatus", playerReadyStatusService.getPlayerReadyStatus());

        if (roomService.saveRoom(room, authentication)) {
            List<Search> searches = searchRepository.findAll();
            searchService.cleanSearches(searches);
            return "redirect:/room/rooms";
        } else {
            return "/room/roomExists";
        }
    }

    @GetMapping("/rooms")
    public String showRoomsAfterAdding(Model model) {
        List<Game> gameList = gameRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<Room> limitedRooms = rooms.stream().limit(200)
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm");

        List<String> formattedCreatedList = rooms.stream()
                .map(room -> room.getCreated().format(formatter))
                .collect(Collectors.toList());

        List<String> formattedExpiresList = rooms.stream()
                .map(room -> room.getExpires().format(formatter))
                .collect(Collectors.toList());

        model.addAttribute("formattedCreatedList", formattedCreatedList);
        model.addAttribute("formattedExpiresList", formattedExpiresList);
        model.addAttribute("rooms", limitedRooms);
        model.addAttribute("gameList", gameList);
        roomService.cleanRooms(rooms);
        return "/room/roomsForUser";
    }


}
