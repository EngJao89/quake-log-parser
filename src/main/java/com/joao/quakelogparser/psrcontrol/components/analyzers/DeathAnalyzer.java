package com.joao.quakelogparser.psrcontrol.components.analyzers;

import com.joao.quakelogparser.model.DeathType;
import com.joao.quakelogparser.model.Player;
import com.joao.quakelogparser.model.PlayerDeathInfo;
import com.joao.quakelogparser.model.PlayerKillInfo;

import java.util.Map;
import java.util.Optional;

public class DeathAnalyzer implements AnalyzerChain {

    private final Optional<AnalyzerChain> next;

    public DeathAnalyzer(final Optional<AnalyzerChain> nextAnalizer) {
        this.next = nextAnalizer;
    }

    @Override
    public void resolve(final String killerId, final String killedId, final String deathTypeId,
                        final Map<String, Player> playerByUserId, final Map<String, PlayerKillInfo> killByName,
                        final Map<String, PlayerDeathInfo> deathByName) {

        process(killedId, deathTypeId, playerByUserId, deathByName);
        next(this.next, killerId, killedId, deathTypeId, playerByUserId, killByName, deathByName);
    }

    private void process(final String killedId, final String deathTypeId, final Map<String, Player> playerByUserId,
                         final Map<String, PlayerDeathInfo> deathByName) {
        final Player killed = playerByUserId.get(killedId);
        if (null == killed) {
            return;
        }

        final String killedName = killed.getName();
        final DeathType deathType = getDeathType(deathTypeId);

        if (!deathByName.containsKey(killedName)) {
            deathByName.put(killedName, new PlayerDeathInfo(killed));
        }

        deathByName.get(killedName).addDeathType(deathType);
    }

    private DeathType getDeathType(final String deathTypeId) {
        try {
            final Integer deathTypeValue = Integer.valueOf(deathTypeId);
            return DeathType.getByValue(deathTypeValue);

        } catch (final Exception exception) {
            System.out.println("Error parsing death type.");
            return DeathType.NOT_FOUND;
        }
    }
}