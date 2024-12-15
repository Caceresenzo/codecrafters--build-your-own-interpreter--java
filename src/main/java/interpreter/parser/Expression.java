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

	public record Binary(
		@NonNull Expression left,
		@NonNull Token operator,
		@NonNull Expression right
	) implements Expression {}

	public record Variable(
		@NonNull Token token
	) implements Expression {}

	public interface Visitor<T> {

		T visitLiteral(Literal literal);

		T visitGrouping(Grouping grouping);

		T visitUnary(Unary unary);

		T visitBinary(Binary binary);

		T visitVariable(Variable variable);

		default T visit(Expression expression) {
			return switch (expression) {
				case Literal literal -> visitLiteral(literal);
				case Grouping grouping -> visitGrouping(grouping);
				case Unary unary -> visitUnary(unary);
				case Binary binary -> visitBinary(binary);
				case Variable variable -> visitVariable(variable);
			};
		}

	}

}