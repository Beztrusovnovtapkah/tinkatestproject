package ru.alitryel.bfmetvennorath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReplayParser {
    private static final Map<Integer, String> colorMap = createColorMap();
    private static final Map<Integer, String> factionMap = createFactionMap();
    private static final Map<Integer, String> allianceMap = createAllianceMap();


    public static Map<String, Object> parseReplay(byte[] data) {
        Map<String, Object> replayInfo = new HashMap<>();

        int timeCreate = readInt32(data, 8);
        replayInfo.put("timeCreate", timeCreate);

        int timestamp = readInt32(data, 16);
        replayInfo.put("time", timestamp / 5);

        int whoWinStartIndex = findStringIndex(data, "X:X:;") + 6;
        int whoWinEndIndex = whoWinStartIndex;

        if (whoWinStartIndex != -1 && whoWinEndIndex != -1) {
            String whoWin = new String(data, whoWinStartIndex, whoWinEndIndex - whoWinStartIndex + 1, StandardCharsets.UTF_8).trim();
            if (whoWin.equals("1")) {
                replayInfo.put("whoWin", "Победа");
            } else {
                replayInfo.put("whoWin", "Поражение");
            }
        }

        int mapStartIndex = findStringIndex(data, "M=");
        int mapEndIndex = findStringIndex(data, ";", mapStartIndex);

        if (mapStartIndex != -1 && mapEndIndex != -1) {
            String mapPath = new String(data, mapStartIndex, mapEndIndex - mapStartIndex + 1, StandardCharsets.UTF_8).trim();
            replayInfo.put("mapPath", mapPath);
        }

        int gsidStartIndex = findStringIndex(data, "GSID=");
        int sdStartIndex = findStringIndex(data, "SD=");

        if (gsidStartIndex != -1 && sdStartIndex != -1) {
            int gsid = readInt32(data, gsidStartIndex + 5);
            int sd = readInt32(data, sdStartIndex + 3);
            replayInfo.put("GSID", gsid);
            replayInfo.put("SD", sd);
        }

        int grStartIndex = findStringIndex(data, "GR=");
        int grEndIndex = findStringIndex(data, ";", grStartIndex);

        if (grStartIndex != -1 && grEndIndex != -1) {
            String grInfo = new String(data, grStartIndex, grEndIndex - grStartIndex + 1, StandardCharsets.UTF_8).trim();
            String[] grValues = grInfo.split(" ");

            if (grValues.length >= 5) {
                int heroesRingEnabled = Integer.parseInt(grValues[0].replaceAll("GR=", ""));
                int createdHeroesEnabled = Integer.parseInt(grValues[2]);
                int commandPoints = Integer.parseInt(grValues[3]);
                int finances = Integer.parseInt(grValues[4]);

                Map<String, Integer> gameRules = new HashMap<>();
                gameRules.put("heroesRingEnabled", heroesRingEnabled);
                gameRules.put("createdHeroesEnabled", createdHeroesEnabled);
                gameRules.put("commandPoints", commandPoints);
                gameRules.put("finances", finances);

                replayInfo.put("gameRules", gameRules);
            }
        }

        int playersStartIndex = findStringIndex(data, "S=H") + 1;
        int playersEndIndex = findStringIndex(data, ";", playersStartIndex);

        if (playersStartIndex != -1 && playersEndIndex != -1) {
            String playersInfo = new String(data, playersStartIndex, playersEndIndex - playersStartIndex + 1, StandardCharsets.UTF_8).trim();
            String[] players = playersInfo.split(":H");
            Map<String, Map<String, String>> playersMap = new HashMap<>();

            for (String player : players) {
                String[] playerInfo = player.split(",");
                if (playerInfo.length == 11) {
                    String nickname = playerInfo[0].replaceAll("S=H", "").replaceAll("=H", "");
                    String uid = playerInfo[1];
                    String port = playerInfo[2];
                    String unknown = playerInfo[3];

                    String color = colorMap.get(Integer.parseInt(playerInfo[4]));
                    String faction = factionMap.get(Integer.parseInt(playerInfo[5]));
                    String position = playerInfo[6];
                    String ally = allianceMap.get(Integer.parseInt(playerInfo[7]));
                    String unknown2 = playerInfo[8];

                    Map<String, String> playerMap = new HashMap<>();
                    playerMap.put("nickname", nickname);
                    playerMap.put("uid", uid);
                    playerMap.put("port", port);
                    playerMap.put("unknown", unknown);
                    playerMap.put("color", color);
                    playerMap.put("faction", faction);
                    playerMap.put("position", position);
                    playerMap.put("ally", ally);
                    playerMap.put("unknown2", unknown2);

                    playersMap.put(uid, playerMap);
                }
            }

            replayInfo.put("players", playersMap);
        }

        return replayInfo;
    }


    private static int readInt32(byte[] data, int startIndex) {
        byte[] intBytes = {data[startIndex], data[startIndex + 1], data[startIndex + 2], data[startIndex + 3]};
        return ByteBuffer.wrap(intBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static int findStringIndex(byte[] data, String searchString) {
        return findStringIndex(data, searchString, 0);
    }

    private static int findStringIndex(byte[] data, String searchString, int startIndex) {
        String dataString = new String(data, startIndex, data.length - startIndex, StandardCharsets.UTF_8);
        int index = dataString.indexOf(searchString);

        if (index != -1) {
            return index + startIndex;
        }

        return -1;
    }



    private static Map<Integer, String> createColorMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(-1, "Случайный");
        map.put(0, "Синий");
        map.put(1, "Красный");
        map.put(2, "Желтый");
        map.put(3, "Зеленый");
        map.put(4, "Оранжевый");
        map.put(5, "Голубой");
        map.put(6, "Фиолетовый");
        map.put(7, "Пурпурный");
        map.put(8, "Белый");
        map.put(9, "Лаймовый");
        return map;
    }

    private static Map<Integer, String> createFactionMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(-1, "Случайно");
        map.put(0, "Гондор");
        map.put(1, "Арнор");
        map.put(2, "Рохан");
        map.put(3, "Лотлориэн");
        map.put(4, "Имладрис");
        map.put(5, "Гномы");
        map.put(6, "Изенгард");
        map.put(7, "Мордор");
        map.put(8, "Мглистые горы");
        map.put(9, "Ангмар");
        map.put(10, "Харад");
        map.put(-2, "Зритель");
        return map;
    }

    private static Map<Integer, String> createAllianceMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(-1, "-");
        map.put(0, "1");
        map.put(1, "2");
        map.put(2, "3");
        map.put(3, "4");
        return map;
    }
}
