package interpreter.parser;

import lombok.NonNull;

public sealed interface Expression {

	public record Literal(
		@NonNull interpreter.parser.Literal value
	) implements Expression {}

}