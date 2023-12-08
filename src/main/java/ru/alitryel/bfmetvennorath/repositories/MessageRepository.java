package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alitryel.bfmetvennorath.entities.Message;
import ru.alitryel.bfmetvennorath.entities.User;


import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findBySender(User user);
}
