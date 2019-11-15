package com.joao.quakelogparser.psrcontrol.components.analyzers;

import com.joao.quakelogparser.model.Player;
import com.joao.quakelogparser.model.PlayerDeathInfo;
import com.joao.quakelogparser.model.PlayerKillInfo;

import java.util.Map;
import java.util.Optional;

public class KillAnalyzer implements AnalyzerChain {

    private final Optional<AnalyzerChain> next;

    public KillAnalyzer(final Optional<AnalyzerChain> nextAnalizer) {
        this.next = nextAnalizer;
    }

    @Override
    public void resolve(final String killerId, final String killedId, final String deathTypeId,
                        final Map<String, Player> playerByUserId, final Map<String, PlayerKillInfo> killByName,
                        final Map<String, PlayerDeathInfo> deathByName) {

        if (!isSuicide(killerId, killedId)) {
            process(killerId, playerByUserId, killByName);
        }

        next(this.next, killerId, killedId, deathTypeId, playerByUserId, killByName, deathByName);
    }

    private void process(final String killerId, final Map<String, Player> playerByUserId,
                         final Map<String, PlayerKillInfo> killByName) {
        final Player killer = playerByUserId.get(killerId);
        if (null == killer) {
            return;
        }

        final String killerName = killer.getName();

        if (!killByName.containsKey(killerName)) {
            killByName.put(killerName, new PlayerKillInfo(killer));
        }

        killByName.get(killerName).incrementCount();
    }

    private boolean isSuicide(final String killerId, final String killedId) {
        return killerId.equals(killedId);
    }
}
