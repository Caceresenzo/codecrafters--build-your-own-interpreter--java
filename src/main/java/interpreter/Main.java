package interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import interpreter.grammar.Scanner;
import interpreter.parser.AstPrinter;
import interpreter.parser.Parser;

public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: ./your_program.sh tokenize <filename>");
			System.exit(1);
		}

		String command = args[0];
		String filePath = args[1];

		String fileContents = "";
		try {
			fileContents = Files.readString(Path.of(filePath));
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
			System.exit(1);
		}

		final var scanner = new Scanner(fileContents);
		final var tokens = scanner.scanTokens();

		switch (command) {
			case "tokenize" -> {
				for (final var token : tokens) {
					System.out.println(token.format());
				}
			}

			case "parse" -> {
				final var parser = new Parser(tokens);
				final var root = parser.parse();

				System.out.println(new AstPrinter().print(root));
			}

			default -> {
				System.err.println("Unknown command: " + command);
				System.exit(1);
			}
		}

		if (scanner.hadError()) {
			System.exit(65);
		}
	}

}