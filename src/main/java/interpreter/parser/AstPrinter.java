package interpreter.parser;

import interpreter.grammar.Literal;

public class AstPrinter implements Expression.Visitor<String> {

	@Override
	public String visitLiteral(Expression.Literal literal) {
		return switch (literal.value()) {
			case Literal.Nil __ -> "nil";
			case Literal.Boolean(final var value) -> Boolean.toString(value);
			case Literal.Number(final var value) -> Double.toString(value);
			case Literal.String(final var value) -> value;
			default -> throw new UnsupportedOperationException();
		};
	}

	public String print(Expression expression) {
		return visit(expression);
	}

}