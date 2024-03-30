package ru.alitryel.testtinka.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;


@Entity
@Data
public class MatchLobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER) // Загрузка турнира немедленно
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "player_match",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private List<Member> players;


    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'Ongoing'")
    private String statusMatch;

    private String resultMatch;
    private Integer round;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp timeMatch;
}
