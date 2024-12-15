package interpreter.parser;

public sealed interface Statement {

	public record Expression(
		interpreter.parser.Expression expression
	) implements Statement {}

	public record Print(
		interpreter.parser.Expression expression
	) implements Statement {}

	public interface Visitor<T> {

		T visitExpression(Expression expression);

		T visitPrint(Print print);

		default T visit(Statement statement) {
			return switch (statement) {
				case Expression expression -> visitExpression(expression);
				case Print print -> visitPrint(print);
			};
		}

	}

}