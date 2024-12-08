package interpreter.grammar;

public record Token(
	TokenType type,
	String lexeme,
	Object literal,
	int line
) {

	public String format() {
		return "%s %s %s".formatted(type, lexeme, literal);
	}

}