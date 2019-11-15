package com.joao.quakelogparser.psrcontrol.components;

import java.util.regex.Pattern;

public class GameReferences {

    private static final String TIME_PATTERN = "([0-9]*[0-9]:[0-5][0-9])";

    public static final String GAME_START_PATTERN = "InitGame";

    public static final String CLIENT_USER_INFO_PATTERN = "ClientUserinfoChanged";

    public static final String KILL_INFO_PATTERN = "Kill";

    public static final String WORLD_KILLER_ID = "1022";

    public static Pattern buildLinePatternFor(final String value) {
        return Pattern.compile(TIME_PATTERN + "\\s(" + value + ":)(.*?)");
    }
}
