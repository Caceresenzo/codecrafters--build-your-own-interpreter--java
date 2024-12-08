package interpreter.grammar;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

	private final String source;

	private final List<Token> tokens;

	private int start;
	private int current;
	private int line;

	private boolean hadError;

	public Scanner(String source) {
		this.source = source;

		this.tokens = new ArrayList<>();

		this.start = 0;
		this.current = 0;
		this.line = 1;
	}

	public List<Token> scanTokens() {
		tokens.clear();

		while (!isAtEnd()) {
			start = current;
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", null, line));

		return tokens;
	}

	private void scanToken() {
		final var character = advance();

		switch (character) {
			case '(' -> addToken(TokenType.LEFT_PAREN);
			case ')' -> addToken(TokenType.RIGHT_PAREN);
			case '{' -> addToken(TokenType.LEFT_BRACE);
			case '}' -> addToken(TokenType.RIGHT_BRACE);
			case ',' -> addToken(TokenType.COMMA);
			case '.' -> addToken(TokenType.DOT);
			case '-' -> addToken(TokenType.MINUS);
			case '+' -> addToken(TokenType.PLUS);
			case ';' -> addToken(TokenType.SEMICOLON);
			case '*' -> addToken(TokenType.STAR);
			case '=' -> {
				if (this.match('=')) {
					addToken(TokenType.EQUAL_EQUAL);
				} else {
					addToken(TokenType.EQUAL);
				}
			}
			default -> error(line, "Unexpected character: %c".formatted(character));
		}
	}

	private char advance() {
		return source.charAt(current++);
	}

	private boolean match(char expected) {
		if (isAtEnd()) {
			return false;
		}

		if (source.charAt(current) != expected) {
			return false;
		}

		++current;
		return true;
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private String text() {
		return source.substring(start, current);
	}

	private void addToken(TokenType type, Object literal) {
		tokens.add(new Token(type, text(), literal, line));
	}

	public boolean isAtEnd() {
		return current >= source.length();
	}

	private void error(int line, String message) {
		report(line, "", message);
	}

	private void report(int line, String where, String message) {
		hadError = true;

		System.err.println("[line %d] Error%s: %s".formatted(line, where, message));
	}

	public boolean hadError() {
		return hadError;
	}

}