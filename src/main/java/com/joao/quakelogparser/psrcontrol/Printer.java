package com.joao.quakelogparser.psrcontrol;

import com.joao.quakelogparser.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Printer {

    private static final String TAB = "    ";

    public static void printGames(final List<Game> games) {
        final Printer printer = new Printer();
        printer.printOverallInfo(games);
        printer.printGameInfo(games);
    }

    private void printOverallInfo(final List<Game> games) {
        System.out.println("Overall: {");

        final Map<String, Integer> killsByName = groupKillsByPlayers(games);
        for (final Map.Entry<String, Integer> entry : killsByName.entrySet()) {
            final String playerName = entry.getKey();
            final Integer kills = entry.getValue();

            System.out.println(TAB + "\"" + playerName + "\": " + kills);
        }

        System.out.println("}");
    }

    private final Map<String, Integer> groupKillsByPlayers(final List<Game> games) {
        final Map<String, Integer> killsByName = new HashMap<>();

        final List<PlayerKillInfo> playerKillInfos = extractPlayerKillInfos(games);

        for (final PlayerKillInfo playerKillInfo : playerKillInfos) {

            final String playerName = playerKillInfo.getPlayer().getName();
            final Integer count = playerKillInfo.getCount();

            if (!killsByName.containsKey(playerName)) {
                killsByName.put(playerName, 0);
            }

            final Integer previousValue = killsByName.get(playerName);
            killsByName.put(playerName, previousValue + count);
        }

        return killsByName;
    }

    private List<PlayerKillInfo> extractPlayerKillInfos(final List<Game> games) {
        return games.stream().map(Game::getPlayerKillInfos).flatMap(List::stream).collect(toList());
    }

    private void printGameInfo(final List<Game> games) {
        for (final Game game : games) {
            printGameName(game);
            printTotalKills(game);
            printPlayers(game);
            printKills(game);
            printKillsByMeans(game);

            System.out.println("}");
        }
    }

    private void printGameName(final Game game) {
        System.out.println(game.getName() + ": {");
    }

    private void printTotalKills(final Game game) {
        final Long totalCount = game.getPlayerKillInfos().stream().map(PlayerKillInfo::getCount)
                .mapToInt(Integer::intValue).count();
        System.out.println(TAB + "total_kills: " + totalCount + ";");
    }

    private void printPlayers(final Game game) {
        final String players = game.getPlayers().stream().map(Player::getName).map(this::appendDoubleQuotes)
                .collect(joining(", "));

        System.out.println(TAB + "players: [" + players + "]");
    }

    private void printKills(final Game game) {
        if (game.getPlayerKillInfos().isEmpty()) {
            return;
        }

        System.out.println(TAB + "Kills: {");

        final Iterator<PlayerKillInfo> iterator = game.getPlayerKillInfos().iterator();

        while (iterator.hasNext()) {
            final PlayerKillInfo playerKillInfo = iterator.next();
            final Player player = playerKillInfo.getPlayer();

            System.out.print(TAB + TAB + "\"" + player.getName() + "\": " + playerKillInfo.getCount());

            if (iterator.hasNext()) {
                System.out.println(",");

            } else {
                System.out.println();
            }
        }

        System.out.println(TAB + "}");
    }

    private void printKillsByMeans(final Game game) {
        if (game.getPlayerDeathInfos().isEmpty()) {
            return;
        }

        System.out.println(TAB + "kills_by_means: {");

        final Map<DeathType, Integer> countByDeathTypes = groupByDeathType(game);
        final Iterator<Map.Entry<DeathType, Integer>> iterator = countByDeathTypes.entrySet().iterator();

        while (iterator.hasNext()) {

            final Map.Entry<DeathType, Integer> countByDeathType = iterator.next();
            final DeathType deathType = countByDeathType.getKey();
            final Integer count = countByDeathType.getValue();

            System.out.print(TAB + TAB + "\"" + deathType + "\": " + count);

            if (iterator.hasNext()) {
                System.out.println(",");

            } else {
                System.out.println();
            }
        }

        System.out.println(TAB + "}");
    }

    private Map<DeathType, Integer> groupByDeathType(final Game game) {

        final List<PlayerDeathInfo> playerDeathInfos = game.getPlayerDeathInfos();
        final Map<DeathType, Integer> countByDeathType = new HashMap<>();

        for (final PlayerDeathInfo playerDeathInfo : playerDeathInfos) {
            final Map<DeathType, Integer> playerCountByDeathTypes = playerDeathInfo.getCountByDeathType();

            for (final Map.Entry<DeathType, Integer> entry : playerCountByDeathTypes.entrySet()) {
                final DeathType deathType = entry.getKey();
                final Integer count = entry.getValue();

                if (!countByDeathType.containsKey(deathType)) {
                    countByDeathType.put(deathType, 0);
                }

                final Integer previous = countByDeathType.get(deathType);
                countByDeathType.put(deathType, previous + count);
            }
        }

        return countByDeathType;
    }

    private String appendDoubleQuotes(final String string) {
        return "\"" + string + "\"";
    }
}

