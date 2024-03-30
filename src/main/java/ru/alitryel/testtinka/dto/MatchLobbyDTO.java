package ru.alitryel.testtinka.dto;

import lombok.Data;
import ru.alitryel.testtinka.entities.Member;
import ru.alitryel.testtinka.entities.Tournament;

import java.sql.Timestamp;
import java.util.List;

@Data
public class MatchLobbyDTO {
    private long id;
    private long tournamentId;
    private List<Long> playerIds;
    private String statusMatch;
    private Timestamp timeMatch;
}

