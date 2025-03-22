package interpreter.grammar;

import lombok.NonNull;

public record Token(
	@NonNull TokenType type,
	@NonNull String lexeme,
	@NonNull Literal literal,
//	Location location
	int line
) {

	public String format() {
		return "%s %s %s".formatted(type, lexeme, literal.format());
	}
//	
//	public int line() {
//		return location.line();
//	}

}