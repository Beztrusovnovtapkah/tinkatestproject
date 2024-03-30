package ru.alitryel.testtinka.dto;

import lombok.Data;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.entities.Member;

import java.util.List;

@Data
public class TournamentDTO {
    private long id;
    private String nameTournament;
    private String descTournament;
    private Integer poolTournament;
    private Integer maxPlayersTournament;
    private List<Member> players;
    private List<MatchLobby> matches;

}