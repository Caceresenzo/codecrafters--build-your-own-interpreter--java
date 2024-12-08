package interpreter.parser;

import lombok.NonNull;

public sealed interface Expression {

	public record Literal(
		@NonNull interpreter.grammar.Literal value
	) implements Expression {}

	public interface Visitor<T> {

		T visitLiteral(Literal value);

		default T visit(Expression expression) {
			return switch (expression) {
				case Expression.Literal literal -> visitLiteral(literal);
			};
		}

	}

}