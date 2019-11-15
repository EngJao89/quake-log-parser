package com.joao.quakelogparser.model;

import java.util.List;

public class Game {

    private final String name;

    private final List<Player> players;

    private final List<PlayerKillInfo> playerKillInfos;

    private final List<PlayerDeathInfo> playerDeathInfos;

    public Game(final String name, final List<Player> players, final List<PlayerKillInfo> playerKillInfos,
                final List<PlayerDeathInfo> playerDeathInfos) {
        this.name = name;
        this.players = players;
        this.playerKillInfos = playerKillInfos;
        this.playerDeathInfos = playerDeathInfos;
    }

    public String getName() {
        return this.name;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<PlayerKillInfo> getPlayerKillInfos() {
        return this.playerKillInfos;
    }

    public List<PlayerDeathInfo> getPlayerDeathInfos() {
        return this.playerDeathInfos;
    }
}

