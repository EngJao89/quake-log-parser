package com.joao.quakelogparser.psrcontrol.components;

import com.joao.quakelogparser.model.SingleGameLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.joao.quakelogparser.psrcontrol.components.GameReferences.GAME_START_PATTERN;
import static com.joao.quakelogparser.psrcontrol.components.GameReferences.buildLinePatternFor;


/**
 * Breaks the given file into game structures.
 */
public class GameSplitter {

    /**
     * Split into {@link SingleGameLog} each bunch of log lines that represents
     * a single game.
     *
     * @param allLogLines
     *            A log line {@link List}.
     * @return A {@link SingleGameLog} {@link List}.
     */
    public static List<SingleGameLog> split(final List<String> allLogLines) {
        return new GameSplitter().splitGames(allLogLines);
    }

    /**
     * Split the given log lines into meaningful {@link SingleGameLog}s.
     *
     * @param allLogLines
     *            The given log lines.
     * @return A {@link SingleGameLog} {@link List}.
     */
    private List<SingleGameLog> splitGames(final List<String> allLogLines) {
        final List<SingleGameLog> gameLogs = new ArrayList<>();

        for (int index = 0; index < allLogLines.size(); index++) {
            final String logLine = allLogLines.get(index).trim();

            if (isGameStart(logLine)) {
                gameLogs.add(new SingleGameLog(buildGameName(gameLogs.size())));
            }

            getLast(gameLogs).ifPresent(gameLog -> gameLog.addLogLine(logLine));
        }

        return gameLogs;
    }

    /**
     * Builds a game name for the given index.
     *
     * <p>
     * e.g.<br/>
     * <b>Index:</b> 1 <br/>
     * <b>Game Name:</b> game-2
     *
     * @param index
     * @return
     */
    private String buildGameName(final int index) {
        return "game-" + (index + 1);
    }

    /**
     * Checks if the given log line is the start of a game.
     *
     * <p>
     * e.g. <br/>
     * 20:37 InitGame: \sv_floodProtect\1\sv_maxPing\0\sv_minPing\0\sv_maxRate
     *
     * @param logLine
     * @return True if it is a game start line. Otherwise false.
     */
    private boolean isGameStart(final String logLine) {
        return buildLinePatternFor(GAME_START_PATTERN).matcher(logLine).matches();
    }

    /**
     * Gets the last {@link SingleGameLog} from the given {@link List}.
     *
     * @param gameLogs
     *            The given {@link Game} {@link List}.
     * @return An {@link Optional} {@link SingleGameLog}.
     */
    private Optional<SingleGameLog> getLast(final List<SingleGameLog> gameLogs) {
        if (null == gameLogs || gameLogs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(gameLogs.get(gameLogs.size() - 1));
    }
}

