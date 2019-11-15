package com.joao.quakelogparser.model;

import java.util.ArrayList;
import java.util.List;


public class SingleGameLog {

    private final String name;

    private List<String> logLines;

    public SingleGameLog(final String name) {
        this.name = name;
        this.logLines = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLogLines() {
        return this.logLines;
    }

    public void addLogLine(final String logLine) {
        this.logLines.add(logLine);
    }
}
