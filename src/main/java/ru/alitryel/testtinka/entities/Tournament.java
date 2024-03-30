package ru.alitryel.testtinka.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nameTournament;
    private String descTournament;
    private Integer poolTournament;
    private Integer maxPlayersTournament;
    @JsonIgnore
    @ManyToMany(mappedBy = "tournaments", fetch = FetchType.EAGER)
    private List<Member> players;

    @JsonIgnore
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MatchLobby> matches;


}
