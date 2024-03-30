package ru.alitryel.testtinka.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nickname;
    @ManyToMany
    @JoinTable(
            name = "player_tournament",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    private List<Tournament> tournaments;

    @ManyToMany
    @JoinTable(
            name = "player_match",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id")
    )
    private List<MatchLobby> matches;
    private String eloMember;
    private String countryMember;
    private String imageMemberUrl;


}
