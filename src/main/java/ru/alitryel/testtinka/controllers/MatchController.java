package ru.alitryel.testtinka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alitryel.testtinka.dto.MatchLobbyDTO;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.services.MatchService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchLobbyService;

    @Autowired
    public MatchController(MatchService matchLobbyService) {
        this.matchLobbyService = matchLobbyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchLobbyDTO> getMatchLobby(@PathVariable long id) {
        Optional<MatchLobby> matchLobbyOptional = matchLobbyService.getMatchLobbyById(id);
        return matchLobbyOptional.map(matchLobby -> ResponseEntity.ok(mapToDto(matchLobby)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MatchLobbyDTO>> getAllMatchLobbies() {
        List<MatchLobbyDTO> matchLobbies = matchLobbyService.getAllMatchLobbies()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(matchLobbies);
    }

    @PostMapping
    public ResponseEntity<MatchLobbyDTO> createMatchLobby(@RequestBody MatchLobbyDTO matchLobbyDTO) {
        MatchLobby createdMatchLobby = matchLobbyService.createMatchLobby(mapToEntity(matchLobbyDTO));
        return new ResponseEntity<>(mapToDto(createdMatchLobby), HttpStatus.CREATED);
    }

    private MatchLobbyDTO mapToDto(MatchLobby matchLobby) {
        MatchLobbyDTO dto = new MatchLobbyDTO();
        dto.setId(matchLobby.getId());
        dto.setTournamentId(matchLobby.getTournament().getId());
        dto.setPlayerIds(matchLobby.getPlayers().stream().map(Member::getId).collect(Collectors.toList()));
        dto.setStatusMatch(matchLobby.getStatusMatch());
        dto.setTimeMatch(matchLobby.getTimeMatch());
        return dto;
    }




    private MatchLobby mapToEntity(MatchLobbyDTO dto) {

        MatchLobby matchLobby = new MatchLobby();
        matchLobby.setStatusMatch(dto.getStatusMatch());
        matchLobby.setTimeMatch(dto.getTimeMatch());

        return matchLobby;
    }
}

