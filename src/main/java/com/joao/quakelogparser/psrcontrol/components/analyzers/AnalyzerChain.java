package com.joao.quakelogparser.psrcontrol.components.analyzers;

import com.joao.quakelogparser.model.Player;
import com.joao.quakelogparser.model.PlayerDeathInfo;
import com.joao.quakelogparser.model.PlayerKillInfo;

import java.util.Map;
import java.util.Optional;

public interface AnalyzerChain {

    void resolve(final String killerId, final String killedId, final String deathTypeId,
                 final Map<String, Player> playerByUserId, final Map<String, PlayerKillInfo> killByName,
                 final Map<String, PlayerDeathInfo> deathByName);

    default void next(final Optional<AnalyzerChain> next, final String killerId, final String killedId,
                      final String deathTypeId, final Map<String, Player> playerByUserId,
                      final Map<String, PlayerKillInfo> killByName, final Map<String, PlayerDeathInfo> deathByName) {

        if (null != next) {
            next.ifPresent(analyzer -> analyzer.resolve(killerId, killedId, deathTypeId, playerByUserId, killByName,
                    deathByName));
        }
    }
}
