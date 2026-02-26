package com.joao.quakelogparser.psrcontrol;

import com.joao.quakelogparser.model.Game;
import com.joao.quakelogparser.model.SingleGameLog;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.joao.quakelogparser.psrcontrol.Printer.printGames;
import static com.joao.quakelogparser.psrcontrol.Reader.readFile;
import static com.joao.quakelogparser.psrcontrol.components.GameSplitter.split;
import static com.joao.quakelogparser.psrcontrol.components.SingleGameParser.parses;

@SpringBootApplication
public class QuakeLogParserquakeLogParserApplication {

	@SuppressWarnings("resource")
	public static void main(final String[] args) throws IOException {

		System.out.print("Enter the log path: ");
		final String path = new Scanner(System.in).next();

		final List<String> allLogLines = readFile(path);
		if (allLogLines.isEmpty()) {
			return;
		}

		final List<SingleGameLog> singleGameLogs = split(allLogLines);
		if (singleGameLogs.isEmpty()) {
			return;
		}

		final List<Game> games = parses(singleGameLogs);
		if (singleGameLogs.isEmpty()) {
			return;
		}

		printGames(games);
	}

}
