package ru.alitryel.testtinka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alitryel.testtinka.entities.MatchLobby;

public interface MatchRepo extends JpaRepository<MatchLobby, Long> {
}
