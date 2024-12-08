package interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import interpreter.grammar.Scanner;
import interpreter.parser.AstPrinter;
import interpreter.parser.Parser;

public class Main {

	public static void tokenize(Lox lox, String content) {
		final var scanner = new Scanner(lox, content);
		final var tokens = scanner.scanTokens();

		for (final var token : tokens) {
			System.out.println(token.format());
		}
	}

	public static void parse(Lox lox, String content) {
		final var scanner = new Scanner(lox, content);
		final var tokens = scanner.scanTokens();

		if (lox.hadError()) {
			return;
		}

		new Parser(lox, tokens)
			.parse()
			.map(new AstPrinter()::print)
			.ifPresent(System.out::println);
	}

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

		final var lox = new Lox();

		switch (command) {
			case "tokenize" -> tokenize(lox, fileContents);
			case "parse" -> parse(lox, fileContents);

			default -> {
				System.err.println("Unknown command: " + command);
				System.exit(1);
			}
		}

		if (lox.hadError()) {
			System.exit(65);
		}
	}

}