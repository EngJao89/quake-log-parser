package com.joao.quakelogparser.psrcontrol.components.analyzers;

import com.joao.quakelogparser.model.Player;
import com.joao.quakelogparser.model.PlayerDeathInfo;
import com.joao.quakelogparser.model.PlayerKillInfo;

import java.util.Map;
import java.util.Optional;

import static com.joao.quakelogparser.psrcontrol.components.GameReferences.WORLD_KILLER_ID;

public class WorldDeathAnalyzer implements AnalyzerChain {

    private final Optional<AnalyzerChain> next;

    public WorldDeathAnalyzer(final Optional<AnalyzerChain> nextAnalizer) {
        this.next = nextAnalizer;
    }

    @Override
    public void resolve(final String killerId, final String killedId, final String deathTypeId,
                        final Map<String, Player> playerByUserId, final Map<String, PlayerKillInfo> killByName,
                        final Map<String, PlayerDeathInfo> deathByName) {

        if (isWorldKiller(killerId)) {
            process(killedId, playerByUserId, killByName);
        }

        next(this.next, killerId, killedId, deathTypeId, playerByUserId, killByName, deathByName);
    }

    private void process(final String killedId, final Map<String, Player> playerByUserId,
                         final Map<String, PlayerKillInfo> killByName) {

        final Player killed = playerByUserId.get(killedId);
        if (null == killed) {
            return;
        }

        final String killedName = killed.getName();

        // Punishment for <world> death
        if (!killByName.containsKey(killedName)) {
            killByName.put(killedName, new PlayerKillInfo(killed));
        }
        killByName.get(killedName).decrementCount();
    }

    private boolean isWorldKiller(final String killerId) {
        return WORLD_KILLER_ID.equalsIgnoreCase(killerId);
    }
}