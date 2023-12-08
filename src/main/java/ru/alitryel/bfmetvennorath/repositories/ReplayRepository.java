package ru.alitryel.bfmetvennorath.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alitryel.bfmetvennorath.entities.Replay;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplayRepository extends JpaRepository<Replay, Long> {

    @Query("SELECT r FROM Replay r WHERE r.playerFirstNickname = :username OR r.playerSecondNickname = :username ORDER BY r.id DESC")
    List<Replay> findTop10ByUsername(@Param("username") String username, Pageable pageable);
}
