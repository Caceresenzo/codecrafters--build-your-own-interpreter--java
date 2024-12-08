package interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import interpreter.evaluating.Interpreter;
import interpreter.grammar.Literal;
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

	public static void evaluate(Lox lox, String content) {
		final var scanner = new Scanner(lox, content);
		final var tokens = scanner.scanTokens();

		if (lox.hadError()) {
			return;
		}

		final var parser = new Parser(lox, tokens);
		final var root = parser.parse();

		if (lox.hadError()) {
			return;
		}

		final var interpreter = new Interpreter(lox);
		final var value = interpreter.evaluate(root.orElseThrow());

		if (!(value instanceof Literal.Boolean)) {
			System.out.println(value);
		}
		System.out.println(value.format());
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: ./your_program.sh tokenize <filename>");
			System.exit(1);
		}

		final var command = args[0];
		final var filePath = args[1];

		String content = "";
		try {
			content = Files.readString(Path.of(filePath));
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
			System.exit(1);
		}

		final var lox = new Lox();

		switch (command) {
			case "tokenize" -> tokenize(lox, content);
			case "parse" -> parse(lox, content);
			case "evaluate" -> evaluate(lox, content);

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