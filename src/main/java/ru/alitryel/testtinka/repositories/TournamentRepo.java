package ru.alitryel.testtinka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alitryel.testtinka.entities.Tournament;

public interface TournamentRepo extends JpaRepository<Tournament, Long> {
}

