package ru.alitryel.testtinka.dto;

import lombok.Data;
import ru.alitryel.testtinka.entities.MatchLobby;
import ru.alitryel.testtinka.entities.Tournament;

import java.util.List;

@Data
public class MemberDTO {

    private long id;
    private String nickname;
    private List<Tournament> tournaments;
    private List<MatchLobby> matches;
    private String eloMember;
    private String countryMember;
    private String imageMemberUrl;
}
