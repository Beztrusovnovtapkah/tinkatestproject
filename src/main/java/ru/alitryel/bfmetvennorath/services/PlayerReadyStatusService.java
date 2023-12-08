package ru.alitryel.bfmetvennorath.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlayerReadyStatusService {

    private final Map<Long, Boolean> playerReadyStatus = new HashMap<>();

    public Map<Long, Boolean> getPlayerReadyStatus() {
        return playerReadyStatus;
    }

    public void setPlayerReadyStatus(Long userId, boolean status) {
        playerReadyStatus.put(userId, status);
    }
}
