package ru.alitryel.bfmetvennorath.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String capitanRoom;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Game> game;

    private LocalDateTime created;

    private LocalDateTime expires;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> userList;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Message> messages;
    private Integer maxCount;
    private boolean gameStarted;
    private boolean replayUploaded;

    private int bansByCaptain;
    private int bansByPlayer;
    private int picksByCaptain;
    private int picksByPlayer;
    private String currentPhase; // Например, "banning" или "picking"
    private Long currentPlayerTurn; // ID текущего игрока, чей ход

}