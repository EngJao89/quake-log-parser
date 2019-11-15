package com.joao.quakelogparser.psrcontrol.components;

import com.joao.quakelogparser.model.*;
import com.joao.quakelogparser.psrcontrol.components.analyzers.AnalyzerChain;
import com.joao.quakelogparser.psrcontrol.components.analyzers.DeathAnalyzer;
import com.joao.quakelogparser.psrcontrol.components.analyzers.KillAnalyzer;
import com.joao.quakelogparser.psrcontrol.components.analyzers.WorldDeathAnalyzer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.joao.quakelogparser.psrcontrol.components.GameReferences.*;

public class SingleGameParser {

    public static List<Game> parses(final List<SingleGameLog> singleGameLogs) {
        return new SingleGameParser().parseSingleGameLogs(singleGameLogs);
    }

    private List<Game> parseSingleGameLogs(final List<SingleGameLog> singleGameLogs) {
        final List<Game> games = new ArrayList<>();

        for (final SingleGameLog singleGameLog : singleGameLogs) {
            games.add(parseSingleGameLog(singleGameLog));
        }

        return games;
    }

    private Game parseSingleGameLog(final SingleGameLog singleGameLog) {
        final Map<String, Player> playerByUserId = new HashMap<>();
        final Map<String, Player> playerByName = new HashMap<>();
        final Map<String, PlayerKillInfo> killByName = new HashMap<>();
        final Map<String, PlayerDeathInfo> deathByName = new HashMap<>();

        final List<String> logLines = singleGameLog.getLogLines();
        parseLines(logLines, playerByUserId, playerByName, killByName, deathByName);

        final String name = singleGameLog.getName();
        final List<Player> players = new ArrayList<>(playerByUserId.values());
        final List<PlayerKillInfo> playerKillInfos = new ArrayList<>(killByName.values());
        final List<PlayerDeathInfo> pplayerDeathInfos = new ArrayList<>(deathByName.values());

        return new Game(name, players, playerKillInfos, pplayerDeathInfos);
    }

    private void parseLines(final List<String> logLines, final Map<String, Player> playerByUserId,
                            final Map<String, Player> playerByName, final Map<String, PlayerKillInfo> killByName,
                            final Map<String, PlayerDeathInfo> deathByName) {

        for (final String logLine : logLines) {
            parseLine(logLine, playerByUserId, playerByName, killByName, deathByName);
        }
    }

    private void parseLine(final String logLine, final Map<String, Player> playerByUserId,
                           final Map<String, Player> playerByName, final Map<String, PlayerKillInfo> killByName,
                           final Map<String, PlayerDeathInfo> deathByName) {

        final Matcher userInfoMatcher = buildLinePatternFor(CLIENT_USER_INFO_PATTERN).matcher(logLine);
        if (userInfoMatcher.matches()) {
            final String userInfo = userInfoMatcher.group(3).trim();
            parseUserInfoLine(userInfo, playerByUserId, playerByName);

            return;
        }

        final Matcher killInfoMatcher = buildLinePatternFor(KILL_INFO_PATTERN).matcher(logLine);
        if (killInfoMatcher.matches()) {
            final String killInfo = killInfoMatcher.group(3).trim();
            parseKillInfoLine(killInfo, playerByUserId, killByName, deathByName);

            return;
        }
    }

    private void parseKillInfoLine(final String killInfo, final Map<String, Player> playerByUserId,
                                   final Map<String, PlayerKillInfo> killByName, final Map<String, PlayerDeathInfo> deathByName) {

        final Matcher matcher = Pattern.compile("([0-9]*)\\s([0-9]*)\\s([0-9]*)(.*)").matcher(killInfo);
        if (!matcher.matches()) {
            return;
        }

        final String killerId = matcher.group(1);
        final String killedId = matcher.group(2);
        final String deathTypeId = matcher.group(3);

        prepareAnalyzerChain().resolve(killerId, killedId, deathTypeId, playerByUserId, killByName, deathByName);
    }

    private AnalyzerChain prepareAnalyzerChain() {
        final AnalyzerChain killAnalyzer = new KillAnalyzer(Optional.empty());
        final AnalyzerChain deathAnalyzer = new DeathAnalyzer(Optional.of(killAnalyzer));
        final AnalyzerChain worldDeathAnalyzer = new WorldDeathAnalyzer(Optional.of(deathAnalyzer));

        return worldDeathAnalyzer;
    }

    private void parseUserInfoLine(final String userInfo, final Map<String, Player> playerByUserId,
                                   final Map<String, Player> playerByName) {

        final String userId = parseUserId(userInfo);
        final String playerName = parsePlayerName(userInfo);

        if (!playerByName.containsKey(playerName)) {
            playerByName.put(playerName, new Player(playerName));
        }

        final Player player = playerByName.get(playerName);
        playerByUserId.put(userId, player);
    }

    private String parseUserId(final String userInfo) {
        if (userInfo.length() <= 0) {
            return "";
        }

        return userInfo.substring(0, 1).trim();
    }

    private String parsePlayerName(final String userInfo) {
        if (userInfo.length() <= 0) {
            return "";
        }

        final int playerNameStart = userInfo.indexOf("n\\");
        if (playerNameStart <= 0) {
            return "";
        }

        final int playerNameEnd = userInfo.indexOf("\\t\\");
        if (playerNameEnd <= 0) {
            return "";
        }

        return userInfo.substring(playerNameStart + 2, playerNameEnd).trim();
    }
}
