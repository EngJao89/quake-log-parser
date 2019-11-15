package com.joao.quakelogparser.psrcontrol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Collections.emptyList;

public class Reader {

    public static List<String> readFile(final String path) {
        return new Reader().getAllLines(path);
    }

    private List<String> getAllLines(final String path) {
        try {
            return Files.readAllLines(Paths.get(path));

        } catch (IOException e) {
            System.out.println("Error reading file");
            return emptyList();
        }
    }
}