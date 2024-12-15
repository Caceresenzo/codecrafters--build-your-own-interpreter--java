package interpreter.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import interpreter.Lox;
import lombok.NonNull;

public class Scanner {

	private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
		Map.entry("and", TokenType.AND),
		Map.entry("class", TokenType.CLASS),
		Map.entry("else", TokenType.ELSE),
		Map.entry("false", TokenType.FALSE),
		Map.entry("for", TokenType.FOR),
		Map.entry("fun", TokenType.FUN),
		Map.entry("if", TokenType.IF),
		Map.entry("nil", TokenType.NIL),
		Map.entry("or", TokenType.OR),
		Map.entry("print", TokenType.PRINT),
		Map.entry("return", TokenType.RETURN),
		Map.entry("super", TokenType.SUPER),
		Map.entry("this", TokenType.THIS),
		Map.entry("true", TokenType.TRUE),
		Map.entry("var", TokenType.VAR),
		Map.entry("while", TokenType.WHILE)
	);

	private final Lox lox;
	private final String source;

	private final List<Token> tokens = new ArrayList<>();

	private int start = 0;
	private int current = 0;
	private int line = 1;

	public Scanner(
		@NonNull Lox lox,
		@NonNull String source
	) {
		this.lox = lox;
		this.source = source;
	}

	public List<Token> scanTokens() {
		tokens.clear();

		while (!isAtEnd()) {
			start = current;
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", new Literal.Nil(), line));

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
			case '!' -> {
				if (match('=')) {
					addToken(TokenType.BANG_EQUAL);
				} else {
					addToken(TokenType.BANG);
				}
			}
			case '=' -> {
				if (match('=')) {
					addToken(TokenType.EQUAL_EQUAL);
				} else {
					addToken(TokenType.EQUAL);
				}
			}
			case '<' -> {
				if (match('=')) {
					addToken(TokenType.LESS_EQUAL);
				} else {
					addToken(TokenType.LESS);
				}
			}
			case '>' -> {
				if (match('=')) {
					addToken(TokenType.GREATER_EQUAL);
				} else {
					addToken(TokenType.GREATER);
				}
			}
			case '/' -> {
				if (match('/')) {
					advanceNextLine();
				} else {
					addToken(TokenType.SLASH);
				}
			}
			case ' ', '\r', '\t' -> {}
			case '\n' -> ++line;
			case '"' -> string();
			default -> {
				if (isDigitCharacter(character)) {
					number();
				} else if (isIdentifierCharacter(character)) {
					identifier();
				} else {
					error(line, "Unexpected character: %c".formatted(character));
				}
			}
		}
	}

	private void string() {
		char character;
		while ((character = peek()) != '"' && !isAtEnd()) {
			if (character == '\n') {
				++line;
			}

			advance();
		}

		if (isAtEnd()) {
			error(line, "Unterminated string.");
			return;
		}

		// closing "
		advance();

		final var value = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, new Literal.String(value));
	}

	private void number() {
		while (isDigitCharacter(peek())) {
			advance();
		}

		if (peek() == '.' && isDigitCharacter(peek(1))) {
			// consume .
			advance();

			while (isDigitCharacter(peek())) {
				advance();
			}
		}

		final var value = Double.parseDouble(text());
		addToken(TokenType.NUMBER, new Literal.Number(value));
	}

	private void identifier() {
		while (isIdentifierCharacter(peek())) {
			advance();
		}

		final var type = KEYWORDS.getOrDefault(text(), TokenType.IDENTIFIER);
		addToken(type);
	}

	private char peek() {
		return peek(0);
	}

	private char peek(int n) {
		final var index = current + n;

		if (index >= source.length()) {
			return '\0';
		}

		return source.charAt(index);
	}

	private char advance() {
		return source.charAt(current++);
	}

	private void advanceNextLine() {
		while (peek() != '\n' && !isAtEnd()) {
			advance();
		}
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
		addToken(type, new Literal.Nil());
	}

	private String text() {
		return source.substring(start, current);
	}

	private void addToken(TokenType type, Literal literal) {
		tokens.add(new Token(type, text(), literal, line));
	}

	public boolean isAtEnd() {
		return current >= source.length();
	}

	public boolean isDigitCharacter(char character) {
		return Character.isDigit(character);
	}

	public boolean isIdentifierCharacter(char character) {
		return character == '_' || Character.isAlphabetic(character) || isDigitCharacter(character);
	}

	private void error(int line, String message) {
		lox.report(line, "", message);
	}

}