package interpreter.parser;

import java.util.List;

import interpreter.grammar.Literal;
import interpreter.parser.Expression.Assign;
import interpreter.parser.Expression.Binary;
import interpreter.parser.Expression.Unary;
import interpreter.parser.Expression.Variable;

public class AstPrinter implements Expression.Visitor<String> {

	@Override
	public String visitLiteral(Expression.Literal literal) {
		return switch (literal.value()) {
			case Literal.Nil __ -> "nil";
			case Literal.Boolean(final var value) -> Boolean.toString(value);
			case Literal.String(final var value) -> value;
			case Literal.Number(final var value) -> Double.toString(value);
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public String visitGrouping(Expression.Grouping grouping) {
		return parenthesize("group", List.of(grouping.expression()));
	}

	@Override
	public String visitUnary(Unary unary) {
		return parenthesize(unary.operator().lexeme(), List.of(unary.right()));
	}

	@Override
	public String visitBinary(Binary binary) {
		return parenthesize(binary.operator().lexeme(), List.of(binary.left(), binary.right()));
	}

	@Override
	public String visitVariable(Variable variable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitAssign(Assign assign) {
		throw new UnsupportedOperationException();
	}

	public String parenthesize(String name, List<Expression> expressions) {
		final var builder = new StringBuilder()
			.append('(')
			.append(name);

		for (final var expression : expressions) {
			builder
				.append(' ')
				.append(visit(expression));
		}

		return builder
			.append(')')
			.toString();
	}

	public String print(Expression expression) {
		return visit(expression);
	}

}