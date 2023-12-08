package ru.alitryel.bfmetvennorath.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.alitryel.bfmetvennorath.ReplayParser;
import ru.alitryel.bfmetvennorath.ResponseVO;
import ru.alitryel.bfmetvennorath.entities.Replay;
import ru.alitryel.bfmetvennorath.entities.User;
import ru.alitryel.bfmetvennorath.repositories.ReplayRepository;
import ru.alitryel.bfmetvennorath.repositories.UserRepository;
import ru.alitryel.bfmetvennorath.services.UserService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;


@Controller
@RequiredArgsConstructor
@RequestMapping("/replays")
public class ReplayController {

    private final UserRepository userRepository;
    private final ReplayRepository replayRepository;
    private final RoomController roomController;
    private static final double K = 20.0;
    private static final String REPLAYS_DIR = "src/main/replays/";


    @GetMapping("/replay/{id}")
    public String getReplayById(@PathVariable Long id, Model model) {
        Optional<Replay> optionalReplay = replayRepository.findById(id);

        Replay replay = optionalReplay.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Replay not found"));

        model.addAttribute("replay", replay);
        return "replays/replayDetails";
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(REPLAYS_DIR).resolve(filename);
        Resource file = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }


    @RequestMapping("/replayForm")
    public String replayForm(Model model, Principal principal) {

        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        model.addAttribute("user", foundUser);

        return "replays/replayForm";
    }

    @PostMapping("/upload")
    public String replayFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long roomId,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Replay replay) {

        Optional<User> user = userRepository.findByUserName(principal.getName());
        User foundUser = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        Map<String, Object> replayInfo = null;
        try {
            byte[] fileBytes = file.getBytes();
            replayInfo = ReplayParser.parseReplay(fileBytes);


            System.out.println(replayInfo);
            model.addAttribute("user", foundUser);
            model.addAttribute("replayInfo", replayInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String timeCreateStr = replayInfo.get("timeCreate").toString();
        long timeCreate = Long.parseLong(timeCreateStr);
        long nowSeconds = System.currentTimeMillis() / 1000L;
        long difference = nowSeconds - timeCreate;

        if (difference <= 10700000) {
            if (containsNickname(replayInfo, foundUser.getUserName())) {

                ResponseVO<String> responseVO = new ResponseVO<>();
                try {
                    if (file.isEmpty()) {
                        responseVO.setCode(1);
                        responseVO.setMessage("Файл пуст");
                    } else {
                        String dotExtendName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));// Получить расширение
                        String fileName = UUID.randomUUID().toString().replace("-", "") + dotExtendName;// Имя UUID + расширение.
                        String filePath = "C:/Users/SystemX/Desktop/bfmejavaproject/src/main//replays/";
                        File path = new File(filePath);
                        if (!path.exists()) {
                            path.mkdirs();
                        }
                        // Загрузить
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filePath + foundUser.getUserName() + fileName)));
                        out.write(file.getBytes());
                        out.flush();
                        out.close();
                        responseVO.setCode(0);
                        responseVO.setMessage("Файл загружен успешно");
                        responseVO.setData(fileName);

                        replay.setNameFileReplay(foundUser.getUserName() + fileName);
                        replay.setMap(replayInfo.get("mapPath").toString());
                        replay.setTimeGame(replayInfo.get("time").toString());
                        replay.setSaveReplayTime(timeCreateStr);
                        Map<String, Map<String, Object>> players = (Map<String, Map<String, Object>>) replayInfo.get("players");

                        if (!players.isEmpty()) {
                            Iterator<Map<String, Object>> iterator = players.values().iterator();

                            if (iterator.hasNext()) {
                                Map<String, Object> firstPlayer = iterator.next();
                                replay.setPlayerFirstNickname(firstPlayer.get("nickname").toString());
                                replay.setPlayerFirstArmy(firstPlayer.get("faction").toString());
                                replay.setPlayerFirstAlly(firstPlayer.get("ally").toString());
                                replay.setPlayerFirstColor(firstPlayer.get("color").toString());
                                if (Objects.equals(firstPlayer.get("nickname").toString(), foundUser.getUserName())) {
                                    replay.setPlayerFirstResult(replayInfo.get("whoWin").toString());
                                } else {
                                    if (replayInfo.get("whoWin").toString() == "Победа") {
                                        replay.setPlayerFirstResult("Поражение");
                                    } else {
                                        replay.setPlayerFirstResult("Победа");
                                    }
                                }
                            }

                            if (iterator.hasNext()) {
                                Map<String, Object> secondPlayer = iterator.next();
                                replay.setPlayerSecondNickname(secondPlayer.get("nickname").toString());
                                replay.setPlayerSecondArmy(secondPlayer.get("faction").toString());
                                replay.setPlayerSecondAlly(secondPlayer.get("ally").toString());
                                replay.setPlayerSecondColor(secondPlayer.get("color").toString());
                                if (Objects.equals(secondPlayer.get("nickname").toString(), foundUser.getUserName())) {
                                    replay.setPlayerSecondResult(replayInfo.get("whoWin").toString());
                                } else {
                                    if (replayInfo.get("whoWin").toString() == "Победа") {
                                        replay.setPlayerSecondResult("Поражение");
                                    } else {
                                        replay.setPlayerFirstResult("Поражение");
                                    }
                                }
                            }
                        }
                        replayRepository.save(replay);
                    }
                } catch (Exception ex) {
                    responseVO.setCode(2);
                    responseVO.setMessage("Ошибка загрузки файла" + ex.getMessage());
                }

                model.addAttribute("replay", replay);
                addStatsPlayer(replay);
                deleteRoom(roomId, principal);
                return "room/roomsForUser";
            }
        }
        return "replays/replayForm";
    }

    public void addStatsPlayer(Replay replay) {
        Optional<User> firstPlayer = userRepository.findByUserName(replay.getPlayerFirstNickname());
        Optional<User> secondPlayer = userRepository.findByUserName(replay.getPlayerSecondNickname());

        User firstUser = firstPlayer.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        User secondUser = secondPlayer.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        // Определение победителя и проигравшего
        String playerFirstResult = replay.getPlayerFirstResult();
        String playerSecondResult = replay.getPlayerSecondResult();

        if ("Победа".equals(playerFirstResult) && "Поражение".equals(playerSecondResult)) {
            // Первый игрок победил
            updateEloRating(firstUser, secondUser, 1, 0);
        } else if ("Поражение".equals(playerFirstResult) && "Победа".equals(playerSecondResult)) {
            // Второй игрок победил
            updateEloRating(firstUser, secondUser, 0, 1);
        }

        if (replay.getPlayerFirstArmy() == "Харад") {
            firstUser.setCountGameHarad(firstUser.getCountGameHarad() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinHarad(firstUser.getCountWinHarad() + 1);
            } else {
                firstUser.setCountLoseHarad(firstUser.getCountLoseHarad() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Арнор") {
            firstUser.setCountGameArnor(firstUser.getCountGameArnor() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinArnor(firstUser.getCountWinArnor() + 1);
            } else {
                firstUser.setCountLoseArnor(firstUser.getCountLoseArnor() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Ангмар") {
            firstUser.setCountGameAngmar(firstUser.getCountGameAngmar() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinAngmar(firstUser.getCountWinAngmar() + 1);
            } else {
                firstUser.setCountLoseAngmar(firstUser.getCountLoseAngmar() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Гномы") {
            firstUser.setCountGameDwarves(firstUser.getCountGameDwarves() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinDwarves(firstUser.getCountWinDwarves() + 1);
            } else {
                firstUser.setCountLoseDwarves(firstUser.getCountLoseDwarves() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Имладрис") {
            firstUser.setCountGameImladris(firstUser.getCountGameImladris() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinAImladris(firstUser.getCountWinAImladris() + 1);
            } else {
                firstUser.setCountLoseImladris(firstUser.getCountLoseImladris() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Изенгард") {
            firstUser.setCountGameIsengard(firstUser.getCountGameIsengard() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinIsengard(firstUser.getCountWinIsengard() + 1);
            } else {
                firstUser.setCountLoseIsengard(firstUser.getCountLoseIsengard() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Мглистые горы") {
            firstUser.setCountGameGoblins(firstUser.getCountGameGoblins() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinGoblins(firstUser.getCountWinGoblins() + 1);
            } else {
                firstUser.setCountLoseGoblins(firstUser.getCountLoseGoblins() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Гондор") {
            firstUser.setCountGameGondor(firstUser.getCountGameGondor() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinGondor(firstUser.getCountWinGondor() + 1);
            } else {
                firstUser.setCountLoseGondor(firstUser.getCountLoseGondor() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Мордор") {
            firstUser.setCountGameMordor(firstUser.getCountGameMordor() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinMordor(firstUser.getCountWinMordor() + 1);
            } else {
                firstUser.setCountLoseMordor(firstUser.getCountLoseMordor() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Лотлориэн") {
            firstUser.setCountGameLotlorien(firstUser.getCountGameLotlorien() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinLotlorien(firstUser.getCountWinLotlorien() + 1);
            } else {
                firstUser.setCountLoseLotlorien(firstUser.getCountLoseLotlorien() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Случайно") {
            firstUser.setCountGameRandom(firstUser.getCountGameRandom() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinRandom(firstUser.getCountWinRandom() + 1);
            } else {
                firstUser.setCountLoseRandom(firstUser.getCountLoseRandom() + 1);
            }
        } else if (replay.getPlayerFirstArmy() == "Рохан") {
            firstUser.setCountGameRohan(firstUser.getCountGameRohan() + 1);
            if (replay.getPlayerFirstResult() == "Победа") {
                firstUser.setCountWinRohan(firstUser.getCountWinRohan() + 1);
            } else {
                firstUser.setCountLoseRohan(firstUser.getCountLoseRohan() + 1);
            }
        }


        if ("Победа".equals(playerFirstResult) && "Поражение".equals(playerSecondResult)) {
            // Первый игрок победил
            updateEloRating(firstUser, secondUser, 1, 0);
        } else if ("Поражение".equals(playerFirstResult) && "Победа".equals(playerSecondResult)) {
            // Второй игрок победил
            updateEloRating(firstUser, secondUser, 0, 1);
        }

        if (replay.getPlayerSecondArmy() == "Харад") {
            secondUser.setCountGameHarad(secondUser.getCountGameHarad() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinHarad(secondUser.getCountWinHarad() + 1);
            } else {
                secondUser.setCountLoseHarad(secondUser.getCountLoseHarad() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Арнор") {
            secondUser.setCountGameArnor(secondUser.getCountGameArnor() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinArnor(secondUser.getCountWinArnor() + 1);
            } else {
                secondUser.setCountLoseArnor(secondUser.getCountLoseArnor() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Ангмар") {
            secondUser.setCountGameAngmar(secondUser.getCountGameAngmar() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinAngmar(secondUser.getCountWinAngmar() + 1);
            } else {
                secondUser.setCountLoseAngmar(secondUser.getCountLoseAngmar() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Гномы") {
            secondUser.setCountGameDwarves(secondUser.getCountGameDwarves() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinDwarves(secondUser.getCountWinDwarves() + 1);
            } else {
                secondUser.setCountLoseDwarves(secondUser.getCountLoseDwarves() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Имладрис") {
            firstUser.setCountGameImladris(firstUser.getCountGameImladris() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinAImladris(secondUser.getCountWinAImladris() + 1);
            } else {
                secondUser.setCountLoseImladris(secondUser.getCountLoseImladris() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Изенгард") {
            secondUser.setCountGameIsengard(secondUser.getCountGameIsengard() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinIsengard(secondUser.getCountWinIsengard() + 1);
            } else {
                secondUser.setCountLoseIsengard(secondUser.getCountLoseIsengard() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Мглистые горы") {
            secondUser.setCountGameGoblins(secondUser.getCountGameGoblins() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinGoblins(secondUser.getCountWinGoblins() + 1);
            } else {
                secondUser.setCountLoseGoblins(secondUser.getCountLoseGoblins() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Гондор") {
            secondUser.setCountGameGondor(secondUser.getCountGameGondor() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinGondor(secondUser.getCountWinGondor() + 1);
            } else {
                secondUser.setCountLoseGondor(secondUser.getCountLoseGondor() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Мордор") {
            secondUser.setCountGameMordor(secondUser.getCountGameMordor() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinMordor(secondUser.getCountWinMordor() + 1);
            } else {
                secondUser.setCountLoseMordor(secondUser.getCountLoseMordor() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Лотлориэн") {
            secondUser.setCountGameLotlorien(secondUser.getCountGameLotlorien() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinLotlorien(secondUser.getCountWinLotlorien() + 1);
            } else {
                secondUser.setCountLoseLotlorien(secondUser.getCountLoseLotlorien() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Случайно") {
            secondUser.setCountGameRandom(secondUser.getCountGameRandom() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinRandom(secondUser.getCountWinRandom() + 1);
            } else {
                secondUser.setCountLoseRandom(secondUser.getCountLoseRandom() + 1);
            }
        } else if (replay.getPlayerSecondArmy() == "Рохан") {
            secondUser.setCountGameRohan(secondUser.getCountGameRohan() + 1);
            if (replay.getPlayerSecondResult() == "Победа") {
                secondUser.setCountWinRohan(secondUser.getCountWinRohan() + 1);
            } else {
                secondUser.setCountLoseRohan(secondUser.getCountLoseRohan() + 1);
            }
        }

        // Сохранение обновленных данных в репозитории
        userRepository.save(firstUser);
        userRepository.save(secondUser);
    }

    private void updateEloRating(User playerA, User playerB, int resultA, int resultB) {
        double expectedScoreA = 1 / (1 + Math.pow(10, (playerB.getElo() - playerA.getElo()) / 400.0));
        double expectedScoreB = 1 / (1 + Math.pow(10, (playerA.getElo() - playerB.getElo()) / 400.0));

        double actualScoreA = resultA;
        double actualScoreB = resultB;

        double newRatingA = playerA.getElo() + K * (actualScoreA - expectedScoreA);
        double newRatingB = playerB.getElo() + K * (actualScoreB - expectedScoreB);

        playerA.setElo((int) newRatingA);
        playerB.setElo((int) newRatingB);
    }

    @PostMapping("/deleteRoom")
    public String deleteRoom(@RequestParam long roomId, Principal principal) {
        // вызов метода deleteRoom из RoomController
        return roomController.deleteRoom(roomId, principal);
    }

    private boolean containsNickname(Map<String, Object> replayInfo, String nickname) {
        Map<String, Map<String, Object>> players = (Map<String, Map<String, Object>>) replayInfo.get("players");
        for (Map<String, Object> player : players.values()) {
            if (nickname.equals(player.get("nickname"))) {
                return true;
            }
        }
        return false;
    }


}
