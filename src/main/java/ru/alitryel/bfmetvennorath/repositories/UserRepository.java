package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alitryel.bfmetvennorath.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT countGameAngmar + countGameArnor + countGameDwarves + countGameGoblins + countGameGondor + countGameHarad + countGameImladris + countGameIsengard + countGameLotlorien + countGameMordor +countGameRandom + countGameRohan as totalgame FROM User WHERE userName = :username")
    Integer getAllGamesCount(@Param("username") String username);
    @Query(value = "SELECT countWinAngmar + countWinAImladris + countWinArnor + countWinDwarves + countWinGoblins + countWinGondor + countWinHarad + countWinIsengard + countWinLotlorien + countWinMordor + countWinRandom + countWinRohan as totalgamewin FROM User WHERE userName = :username")
    Integer getAllLoseGamesCount(@Param("username") String username);
    @Query(value = "SELECT countLoseAngmar + countLoseArnor + countLoseDwarves + countLoseGoblins + countLoseGondor + countLoseHarad + countLoseImladris + countLoseIsengard + countLoseLotlorien + countLoseMordor + countLoseRandom + countLoseRohan as totalgamelose FROM User WHERE userName = :username")
    Integer getAllWinGamesCount(@Param("username") String username);

    boolean existsByEmail(String email);

    boolean existsByUserName(String username);

    List<User> findAllByOrderByEloDesc();
}