package com.joao.quakelogparser;

import com.joao.quakelogparser.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.joao.quakelogparser.psrcontrol.components.SingleGameParser.parses;

public class SingleGameParserTest {

    private static final Double PRECISION = 0.0000001;

    @Test
    public void shouldParsePlayerKilledByWorld() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:54 Kill: 1022 2 22: <world> killed Isgalamido by MOD_TRIGGER_HURT");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals("game-1", games.get(0).getName());

        final List<Player> players = games.get(0).getPlayers();
        Assertions.assertEquals(1, players.size());
        Assertions.assertEquals("Isgalamido", players.get(0).getName());

        final List<PlayerDeathInfo> playerDeathInfos = games.get(0).getPlayerDeathInfos();
        Assertions.assertEquals(1, playerDeathInfos.size());
        Assertions.assertEquals("Isgalamido", playerDeathInfos.get(0).getPlayer().getName());

        final Map<DeathType, Integer> countByDeathType = games.get(0).getPlayerDeathInfos().get(0)
                .getCountByDeathType();
        Assertions.assertEquals(1, countByDeathType.size());

        final List<Integer> counts = new ArrayList<>(countByDeathType.values());
        Assertions.assertEquals(1, counts.size());
        Assertions.assertEquals(1, counts.get(0), 0.0001);

        final List<DeathType> deathTypes = new ArrayList<>(countByDeathType.keySet());
        Assertions.assertEquals(1, deathTypes.size());
        Assertions.assertEquals(DeathType.MOD_TRIGGER_HURT, deathTypes.get(0));
    }

    @Test
    public void shouldParsePlayerKilledByWorldDeathInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:54 Kill: 1022 2 22: <world> killed Isgalamido by MOD_TRIGGER_HURT");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerDeathInfo> playerDeathInfos = games.get(0).getPlayerDeathInfos();
        Assertions.assertEquals(1, playerDeathInfos.size());
        Assertions.assertEquals("Isgalamido", playerDeathInfos.get(0).getPlayer().getName());

        final Map<DeathType, Integer> countByDeathType = games.get(0).getPlayerDeathInfos().get(0)
                .getCountByDeathType();
        Assertions.assertEquals(1, countByDeathType.size());

        final List<Integer> counts = new ArrayList<>(countByDeathType.values());
        Assertions.assertEquals(1, counts.size());
        Assertions.assertEquals(1, counts.get(0), PRECISION);

        final List<DeathType> deathTypes = new ArrayList<>(countByDeathType.keySet());
        Assertions.assertEquals(1, deathTypes.size());
        Assertions.assertEquals(DeathType.MOD_TRIGGER_HURT, deathTypes.get(0));
    }

    @Test
    public void shouldParsePlayerKilledByWorldKillInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:54 Kill: 1022 2 22: <world> killed Isgalamido by MOD_TRIGGER_HURT");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerKillInfo> playerKillInfos = games.get(0).getPlayerKillInfos();
        Assertions.assertEquals(1, playerKillInfos.size());
        Assertions.assertEquals("Isgalamido", playerKillInfos.get(0).getPlayer().getName());

        final PlayerKillInfo playerKillInfo = playerKillInfos.get(0);
        Assertions.assertEquals("Isgalamido", playerKillInfo.getPlayer().getName());

        final Integer count = playerKillInfo.getCount();
        Assertions.assertEquals(-1, count, PRECISION);
    }

    @Test
    public void shouldParsePlayerKilledByHimselfDeathInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("22:18 Kill: 2 2 7: Isgalamido killed Isgalamido by MOD_ROCKET_SPLASH");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerDeathInfo> playerDeathInfos = games.get(0).getPlayerDeathInfos();
        Assertions.assertEquals(1, playerDeathInfos.size());
        Assertions.assertEquals("Isgalamido", playerDeathInfos.get(0).getPlayer().getName());

        final Map<DeathType, Integer> countByDeathType = games.get(0).getPlayerDeathInfos().get(0)
                .getCountByDeathType();
        Assertions.assertEquals(1, countByDeathType.size());

        final List<Integer> counts = new ArrayList<>(countByDeathType.values());
        Assertions.assertEquals(1, counts.size());
        Assertions.assertEquals(1, counts.get(0), PRECISION);

        final List<DeathType> deathTypes = new ArrayList<>(countByDeathType.keySet());
        Assertions.assertEquals(1, deathTypes.size());
        Assertions.assertEquals(DeathType.MOD_ROCKET_SPLASH, deathTypes.get(0));
    }

    @Test
    public void shouldParsePlayerKilledByHimselfKillInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("22:18 Kill: 2 2 7: Isgalamido killed Isgalamido by MOD_ROCKET_SPLASH");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerKillInfo> playerKillInfos = games.get(0).getPlayerKillInfos();
        Assertions.assertEquals(0, playerKillInfos.size());
    }

    @Test
    public void shouldParseMultiplePlayers() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:35 ClientUserinfoChanged: 3 n\\Mocinha\\t\\0");
        singleGameLog.addLogLine("22:06 Kill: 2 3 7: Isgalamido killed Mocinha by MOD_ROCKET_SPLASH");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<Player> players = games.get(0).getPlayers();
        Assertions.assertEquals(2, players.size());
        Assertions.assertEquals("Isgalamido", players.get(0).getName());
        Assertions.assertEquals("Mocinha", players.get(1).getName());
    }

    @Test
    public void shouldParsePlayerKilledByAnotherPlayerDeathInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:35 ClientUserinfoChanged: 3 n\\Mocinha\\t\\0");
        singleGameLog.addLogLine("22:06 Kill: 2 3 7: Isgalamido killed Mocinha by MOD_ROCKET_SPLASH");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerDeathInfo> playerDeathInfos = games.get(0).getPlayerDeathInfos();
        Assertions.assertEquals(1, playerDeathInfos.size());
        Assertions.assertEquals("Mocinha", playerDeathInfos.get(0).getPlayer().getName());

        final Map<DeathType, Integer> countByDeathType = games.get(0).getPlayerDeathInfos().get(0)
                .getCountByDeathType();
        Assertions.assertEquals(1, countByDeathType.size());

        final List<Integer> counts = new ArrayList<>(countByDeathType.values());
        Assertions.assertEquals(1, counts.size());
        Assertions.assertEquals(1, counts.get(0), PRECISION);

        final List<DeathType> deathTypes = new ArrayList<>(countByDeathType.keySet());
        Assertions.assertEquals(1, deathTypes.size());
        Assertions.assertEquals(DeathType.MOD_ROCKET_SPLASH, deathTypes.get(0));
    }

    @Test
    public void shouldParsePlayerKilledByAnotherPlayerKillInfo() {
        final SingleGameLog singleGameLog = new SingleGameLog("game-1");
        singleGameLog.addLogLine("0:00 InitGame: \\sv_floodProtect\\1\\sv_m");
        singleGameLog.addLogLine("20:34 ClientUserinfoChanged: 2 n\\Isgalamido\\t\\0");
        singleGameLog.addLogLine("20:35 ClientUserinfoChanged: 3 n\\Mocinha\\t\\0");
        singleGameLog.addLogLine("22:06 Kill: 2 3 7: Isgalamido killed Mocinha by MOD_ROCKET_SPLASH");

        final List<Game> games = parses(Arrays.asList(singleGameLog));

        final List<PlayerKillInfo> playerKillInfos = games.get(0).getPlayerKillInfos();
        Assertions.assertEquals(1, playerKillInfos.size());
        Assertions.assertEquals("Isgalamido", playerKillInfos.get(0).getPlayer().getName());

        final PlayerKillInfo playerKillInfo = playerKillInfos.get(0);
        Assertions.assertEquals("Isgalamido", playerKillInfo.getPlayer().getName());

        final Integer count = playerKillInfo.getCount();
        Assertions.assertEquals(1, count, PRECISION);
    }
}
