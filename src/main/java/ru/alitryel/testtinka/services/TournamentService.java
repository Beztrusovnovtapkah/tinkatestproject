package ru.alitryel.testtinka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.alitryel.testtinka.dto.MemberDTO;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.entities.Tournament;
import ru.alitryel.testtinka.repositories.MatchRepo;
import ru.alitryel.testtinka.repositories.TournamentRepo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepo tournamentRepo;
    private final MatchRepo matchRepo;

    @Autowired
    public TournamentService(TournamentRepo tournamentRepository, MatchRepo matchRepo) {
        this.tournamentRepo = tournamentRepository;
        this.matchRepo = matchRepo;
    }

    public Optional<Tournament> getTournamentById(long id) {
        return tournamentRepo.findById(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepo.findAll();
    }

    public Tournament createTournament(Tournament tournament) {
        return tournamentRepo.save(tournament);
    }
    public MemberDTO registerParticipant(long tournamentId, MemberDTO participantDTO) {return participantDTO;}

    @Transactional
    public void generateMatches(long tournamentId) {
        System.out.println("Current transaction status: {}" + TransactionSynchronizationManager.getCurrentTransactionName());
        Tournament tournament = tournamentRepo.findById(tournamentId).orElse(null);

        if (tournament != null) {
            int participantsCount = tournament.getPlayers().size();
            int rounds = calculateRounds(participantsCount);

            for (int round = 1; round <= rounds; round++) {
                generateMatchesForRound(tournament, round);
            }
        }
    }

    private Member determineWinner(MatchLobby match) {
        String matchResult = match.getResultMatch();

        if (matchResult.equals("WINNER_1")) {
            return match.getPlayers().get(0);
        } else if (matchResult.equals("WINNER_2")) {
            return match.getPlayers().get(1);
        } else {
            return null;
        }
    }

    private int calculateRounds(int participantsCount) {
        int rounds = 0;
        int participants = participantsCount;

        while (participants > 1) {
            participants = participants / 2;
            rounds++;
        }

        return rounds;
    }

    private void generateMatchesForRound(Tournament tournament, int round) {
        List<Member> participants = tournament.getPlayers();
        int matchesCount = participants.size() / 2;

        for (int i = 0; i < matchesCount; i++) {
            Member player1 = participants.get(i * 2);
            Member player2 = participants.get(i * 2 + 1);

            MatchLobby match = new MatchLobby();
            match.setTournament(tournament);
            match.setRound(round);
            match.setPlayers(Arrays.asList(player1, player2));
            match.setStatusMatch("Scheduled");
            matchRepo.save(match);
            System.out.println("Saved match with id:" + match.getId());
        }
    }

    }
