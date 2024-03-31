package ru.alitryel.testtinka.controllers;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alitryel.testtinka.dto.MatchLobbyDTO;
import ru.alitryel.testtinka.dto.MemberDTO;
import ru.alitryel.testtinka.dto.TournamentDTO;
import ru.alitryel.testtinka.entities.Tournament;
import ru.alitryel.testtinka.services.RoundService;
import ru.alitryel.testtinka.services.TournamentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Transactional
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final RoundService roundService;
    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService, RoundService roundService) {
        this.tournamentService = tournamentService;
        this.roundService = roundService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournament(@PathVariable long id) {
        Optional<Tournament> tournamentOptional = tournamentService.getTournamentById(id);
        return tournamentOptional.map(tournament -> ResponseEntity.ok(mapToDto(tournament)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<TournamentDTO> tournaments = tournamentService.getAllTournaments()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        Tournament createdTournament = tournamentService.createTournament(mapToEntity(tournamentDTO));
        return new ResponseEntity<>(mapToDto(createdTournament), HttpStatus.CREATED);
    }

    @PostMapping("/{tournamentId}/participants")
    public ResponseEntity<MemberDTO> registerParticipant(
            @PathVariable long tournamentId,
            @RequestBody MemberDTO participantDTO) {
        MemberDTO registeredParticipant = tournamentService.registerParticipant(tournamentId, participantDTO);
        return new ResponseEntity<>(registeredParticipant, HttpStatus.CREATED);
    }

    @PostMapping("/{tournamentId}/generate-matches")
    public ResponseEntity<MatchLobbyDTO> generateMatches(@PathVariable long tournamentId) {
        roundService.webGenerator(tournamentId);
        return new ResponseEntity<>(new MatchLobbyDTO(), HttpStatus.OK);
    }

    private TournamentDTO mapToDto(Tournament tournament) {
        TournamentDTO dto = new TournamentDTO();
        dto.setId(tournament.getId());
        dto.setNameTournament(tournament.getNameTournament());
        dto.setDescTournament(tournament.getDescTournament());
        dto.setPoolTournament(tournament.getPoolTournament());
        dto.setMaxPlayersTournament(tournament.getMaxPlayersTournament());
        return dto;
    }

    private Tournament mapToEntity(TournamentDTO dto) {
        Tournament tournament = new Tournament();
        tournament.setNameTournament(dto.getNameTournament());
        tournament.setDescTournament(dto.getDescTournament());
        tournament.setPoolTournament(dto.getPoolTournament());
        tournament.setMaxPlayersTournament(dto.getMaxPlayersTournament());
        return tournament;
    }
}
