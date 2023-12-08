package ru.alitryel.bfmetvennorath.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    //ID пользователя
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Никнейм пользователя
    @NotBlank
    @Size(min = 3, max = 20)
    private String userName;

    //Email
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    //Пароль
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    //Дата создания
    private LocalDateTime created;

    //Активный или нет
    @NotNull
    private boolean isActive;

    //Список игр
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Game> gameList;

    private String clan;
    //Роли
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    //Рейтинг очки
    @Column(name="elo")
    private Integer elo = 1000;
    //Геймренджер ID
    private Long gamerangerId;
    //IP
    private String userIP;
    private Integer bans;
    //Дискорд
    private String socialDiscord;
    //Титул
    @Column(name="title")
    private String title = "Novice";
    //Время сыгранное в рейт.матчах
    @Column(name="timeCount")
    private Integer timeCount = 0;

    //Случайно
    @Column(name="countGameRandom")
    private Integer countGameRandom = 0;
    @Column(name="countWinRandom")
    private Integer countWinRandom = 0;
    @Column(name="countLoseRandom")
    private Integer countLoseRandom = 0;
    //Гондор
    @Column(name="countGameGondor")
    private Integer countGameGondor = 0;
    @Column(name="countWinGondor")
    private Integer countWinGondor = 0;
    @Column(name="countLoseGondor")
    private Integer countLoseGondor = 0;
    //Арнор
    @Column(name="countGameArnor")
    private Integer countGameArnor = 0;
    @Column(name="countWinArnor")
    private Integer countWinArnor = 0;
    @Column(name="countLoseArnor")
    private Integer countLoseArnor = 0;
    //Рохан
    @Column(name="countGameRohan")
    private Integer countGameRohan = 0;
    @Column(name="countWinRohan")
    private Integer countWinRohan = 0;
    @Column(name="countLoseRohan")
    private Integer countLoseRohan = 0;
    //Лотлориэн
    @Column(name="countGameLotlorien")
    private Integer countGameLotlorien = 0;
    @Column(name="countWinLotlorien")
    private Integer countWinLotlorien = 0;
    @Column(name="countLoseLotlorien")
    private Integer countLoseLotlorien = 0;
    //Имладрис
    @Column(name="countGameImladris")
    private Integer countGameImladris = 0;
    @Column(name="countWinAImladris")
    private Integer countWinAImladris = 0;
    @Column(name="countLoseImladris")
    private Integer countLoseImladris = 0;
    //Гномы
    @Column(name="countGameDwarves")
    private Integer countGameDwarves = 0;
    @Column(name="countWinDwarves")
    private Integer countWinDwarves = 0;
    @Column(name="countLoseDwarves")
    private Integer countLoseDwarves = 0;
    //Изенгард
    @Column(name="countGameIsengard")
    private Integer countGameIsengard = 0;
    @Column(name="countWinIsengard")
    private Integer countWinIsengard = 0;
    @Column(name="countLoseIsengard")
    private Integer countLoseIsengard = 0;
    //Мордор
    @Column(name="countGameMordor")
    private Integer countGameMordor = 0;
    @Column(name="countWinMordor")
    private Integer countWinMordor = 0;
    @Column(name="countLoseMordor")
    private Integer countLoseMordor = 0;
    //МглистыеГоры
    @Column(name="countGameGoblins")
    private Integer countGameGoblins = 0;
    @Column(name="countWinGoblins")
    private Integer countWinGoblins = 0;
    @Column(name="countLoseGoblins")
    private Integer countLoseGoblins = 0;
    //Ангмар
    @Column(name="countGameAngmar")
    private Integer countGameAngmar = 0;
    @Column(name="countWinAngmar")
    private Integer countWinAngmar = 0;
    @Column(name="countLoseAngmar")
    private Integer countLoseAngmar = 0;
    //Харад
    @Column(name="countGameHarad")
    private Integer countGameHarad = 0;
    @Column(name="countWinHarad")
    private Integer countWinHarad = 0;
    @Column(name="countLoseHarad")
    private Integer countLoseHarad = 0;
}