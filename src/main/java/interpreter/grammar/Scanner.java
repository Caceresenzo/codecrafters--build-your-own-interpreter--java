package interpreter.grammar;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

	private final String source;

	private final List<Token> tokens;

	private int start;
	private int current;
	private int line;

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
		}
	}

	private char advance() {
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		final var text = source.substring(start, current);

		tokens.add(new Token(type, text, literal, line));
	}

	public boolean isAtEnd() {
		return current >= source.length();
	}

}