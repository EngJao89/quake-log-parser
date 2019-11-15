package com.joao.quakelogparser.model;

public class PlayerKillInfo {

    private final Player player;

    private Integer count;

    public PlayerKillInfo(final Player player) {
        this.player = player;
        this.count = 0;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Integer getCount() {
        return this.count;
    }

    public void incrementCount() {
        this.count++;
    }

    public void decrementCount() {
        this.count--;
    }
}