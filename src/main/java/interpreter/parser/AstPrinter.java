package interpreter.parser;

import interpreter.grammar.Literal;

public class AstPrinter implements Expression.Visitor<String> {

	@Override
	public String visitLiteral(Expression.Literal literal) {
		return switch (literal.value()) {
			case Literal.Nil __ -> "nil";
			case Literal.Boolean(final var value) -> Boolean.toString(value);
			default -> throw new UnsupportedOperationException();
		};
	}

	public String print(Expression expression) {
		return visit(expression);
	}

}