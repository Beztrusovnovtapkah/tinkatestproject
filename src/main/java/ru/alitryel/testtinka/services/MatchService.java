package ru.alitryel.testtinka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.repositories.MatchRepo;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    private final MatchRepo matchRepository;

    @Autowired
    public MatchService(MatchRepo matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Optional<MatchLobby> getMatchLobbyById(long id) {
        return matchRepository.findById(id);
    }

    public List<MatchLobby> getAllMatchLobbies() {
        return matchRepository.findAll();
    }

    public MatchLobby createMatchLobby(MatchLobby matchLobby) {
        return matchRepository.save(matchLobby);
    }

}
