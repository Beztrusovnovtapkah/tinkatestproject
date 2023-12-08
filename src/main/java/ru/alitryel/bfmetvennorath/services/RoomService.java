package ru.alitryel.bfmetvennorath.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.alitryel.bfmetvennorath.entities.Room;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.RoomRepository;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public boolean saveRoom(Room room, Authentication authentication) {
        User user = userRepository.findByUserName(authentication.getName()).orElse(null);
        if (user == null) {
            return false;
        }
        Room foundRoom = roomRepository.findByName(room.getName()).orElse(null);
        if (foundRoom != null) {
            return false;
        }
        room.setCreated(LocalDateTime.now());
        room.setUserList(Arrays.asList(user));
        room.setExpires(LocalDateTime.now().plusDays(2));
        roomRepository.save(room);

        return true;
    }

    public void cleanRooms(List<Room> roomList) {
        roomList.removeIf(room -> room.getExpires().isAfter(LocalDateTime.now()));
    }

    public Room findRoomById(long id) {
        Optional<Room> room = roomRepository.findById(id);
        Room foundRoom = room.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room does not exist"));
        return foundRoom;
    }

}
