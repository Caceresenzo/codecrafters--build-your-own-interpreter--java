package interpreter.parser;

import interpreter.grammar.Token;
import lombok.NonNull;

public sealed interface Expression {

	public record Literal(
		@NonNull interpreter.grammar.Literal value
	) implements Expression {}

	public record Grouping(
		@NonNull Expression expression
	) implements Expression {}

	public record Unary(
		@NonNull Token operator,
		@NonNull Expression right
	) implements Expression {}

	public interface Visitor<T> {

		T visitLiteral(Literal literal);

		T visitGrouping(Grouping grouping);

		T visitUnary(Unary unary);

		default T visit(Expression expression) {
			return switch (expression) {
				case Expression.Literal literal -> visitLiteral(literal);
				case Expression.Grouping grouping -> visitGrouping(grouping);
				case Expression.Unary unary -> visitUnary(unary);
			};
		}

	}

}