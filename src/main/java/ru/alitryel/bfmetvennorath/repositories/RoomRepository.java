package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.alitryel.bfmetvennorath.entities.Room;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);
    @Query("SELECT COUNT(r) > 0 FROM Room r JOIN r.userList u WHERE r.id = :roomId AND u.id = :userId")
    boolean isUserInRoom(@Param("roomId") long roomId, @Param("userId") long userId);
    boolean existsByCapitanRoom(String userName);
}
