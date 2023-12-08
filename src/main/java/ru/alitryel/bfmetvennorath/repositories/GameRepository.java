package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alitryel.bfmetvennorath.entities.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}