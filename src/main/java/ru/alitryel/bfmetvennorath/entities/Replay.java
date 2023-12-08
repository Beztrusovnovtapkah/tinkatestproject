package ru.alitryel.bfmetvennorath.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Replay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nameFileReplay;
    private String map;
    private String timeGame;
    private String saveReplayTime;
    private String playerFirstNickname;
    private String playerSecondNickname;
    private String playerFirstArmy;
    private String playerSecondArmy;
    private String playerFirstAlly;
    private String playerSecondAlly;
    private String playerFirstColor;
    private String playerSecondColor;
    private String playerFirstResult;
    private String playerSecondResult;
}
