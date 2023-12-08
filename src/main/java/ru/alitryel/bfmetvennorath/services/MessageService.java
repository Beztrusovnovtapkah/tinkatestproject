package ru.alitryel.bfmetvennorath.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.alitryel.bfmetvennorath.components.OutputMessage;
import ru.alitryel.bfmetvennorath.entities.Message;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.MessageRepository;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final RoomService roomService;

    public boolean saveMessage(Authentication authentication, @Valid Message message) {
        String content = message.getContent();
        User user = userService.findUserByName(authentication.getName());
        message.setSent(LocalDateTime.now());
        message.setSender(user);
        message.setContent(authentication.getName() + ":" + " " + content);
        messageRepository.save(message);
        return true;
    }
    public void saveMessageFromWebSocket(OutputMessage outputMessage){
        Message message = new Message();
        message.setRoom(roomService.findRoomById(outputMessage.getRoom()));
        message.setContent(outputMessage.getText());
        message.setSent(LocalDateTime.now());
        message.setSender(userService.findUserByName(outputMessage.getSender()));
        messageRepository.save(message);
    }
}
