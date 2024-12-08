package interpreter.grammar;

import interpreter.parser.Literal;
import lombok.NonNull;

public record Token(
	@NonNull TokenType type,
	@NonNull String lexeme,
	@NonNull Literal literal,
	int line
) {

	public String format() {
		return "%s %s %s".formatted(type, lexeme, literal.format());
	}

}