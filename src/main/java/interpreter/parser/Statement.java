package interpreter.parser;

import java.util.Optional;

import interpreter.grammar.Token;
import lombok.NonNull;

public sealed interface Statement {

	public record Expression(
		@NonNull interpreter.parser.Expression expression
	) implements Statement {}

	public record Print(
		@NonNull interpreter.parser.Expression expression
	) implements Statement {}

	public record Variable(
		@NonNull Token name,
		@NonNull Optional<interpreter.parser.Expression> initializer
	) implements Statement {}

	public interface Visitor<T> {

		T visitExpression(Expression expression);

		T visitPrint(Print print);

		T visitVariable(Variable variable);

		default T visit(Statement statement) {
			return switch (statement) {
				case Expression expression -> visitExpression(expression);
				case Print print -> visitPrint(print);
				case Variable variable -> visitVariable(variable);
			};
		}

	}

}