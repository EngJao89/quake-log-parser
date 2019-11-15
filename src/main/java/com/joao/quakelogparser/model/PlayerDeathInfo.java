package com.joao.quakelogparser.model;

import java.util.HashMap;
import java.util.Map;

public class PlayerDeathInfo {

    private final Player player;

    private final Map<DeathType, Integer> countByDeathType;

    public PlayerDeathInfo(final Player player) {
        this.player = player;
        this.countByDeathType = new HashMap<>();
    }

    public Player getPlayer() {
        return this.player;
    }

    public Map<DeathType, Integer> getCountByDeathType() {
        return this.countByDeathType;
    }

    public void addDeathType(final DeathType deathType) {
        if (!this.countByDeathType.containsKey(deathType)) {
            this.countByDeathType.put(deathType, 0);
        }

        final Integer previousValue = this.countByDeathType.get(deathType);
        this.countByDeathType.put(deathType, previousValue + 1);
    }
}