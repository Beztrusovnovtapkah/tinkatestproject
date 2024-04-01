package ru.alitryel.testtinka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.entities.Tournament;
import ru.alitryel.testtinka.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoundService {
    private final MatchRepo matchRepo;
    private final TournamentRepo tournamentRepo;

    @Autowired
    public RoundService( MatchRepo matchRepo, TournamentRepo tournamentRepo) {
        this.matchRepo = matchRepo;
        this.tournamentRepo = tournamentRepo;
    }
    public void generateRound(Tournament nowTournament, Integer currentRound, List<Member> players){
        for (int i=0; i<players.size()-1; i+=2) {
            MatchLobby matchLobby = new MatchLobby();
            matchLobby.setResultMatch("0");
            matchLobby.setTournament(nowTournament);
            matchLobby.setRound(currentRound);
            List<Member> tmp = new ArrayList<>(players.subList(i, i+2));
            matchLobby.setPlayers(tmp);
            matchLobby.setStatusMatch("ongoing");
            matchRepo.save(matchLobby);
        }
    }
    public void webGenerator(Long idTournament) {
        Tournament nowTournament = tournamentRepo.getById(idTournament);
        List<Member> players = nowTournament.getPlayers();
        //generateRound(nowTournament, round, players);
        int startplay =  players.size();
        for (int m = 1; m < startplay/2; m++) {
            int round = m;
            List<MatchLobby> curMatches = nowTournament.getMatches();
           /* if (round==2) {players.remove(0);
            players.remove(2);
            players.remove(4);}
            else if (round ==3){
                players.remove(0);
                players.remove(1);
            }*/
                generateRound(nowTournament, round, players);
            for (int i = 0; i < curMatches.size(); i++) {
                MatchLobby nowMatchLobby = curMatches.get(i);
                List<Member> matchPlayers = nowMatchLobby.getPlayers();
                if (nowMatchLobby.getResultMatch().equals("Win") && nowMatchLobby.getStatusMatch().equals("ended") ) {
                    for (int j = 0; j < players.size(); j++) {
                        if (players.get(j) == matchPlayers.get(0)) {
                            players.remove(j);
                            break;
                        }
                    }
                } else
                    //if (nowMatchLobby.getResultMatch() == "Lose" && nowMatchLobby.getStatusMatch() == "ended")
                    {
                    for (int j = 0; j < players.size(); j++) {
                        if (players.get(j) == matchPlayers.get(1)) {
                            players.remove(j);
                            break;
                        }
                    }
                }
            }

        }
    }
}
