package interpreter.parser;

import java.util.List;

import interpreter.grammar.Literal;

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
	public String visitUnary(Expression.Unary unary) {
		return parenthesize(unary.operator().lexeme(), List.of(unary.right()));
	}

	@Override
	public String visitBinary(Expression.Binary binary) {
		return parenthesize(binary.operator().lexeme(), List.of(binary.left(), binary.right()));
	}

	@Override
	public String visitVariable(Expression.Variable variable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitAssign(Expression.Assign assign) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitLogical(Expression.Logical logical) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitCall(Expression.Call call) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitGet(Expression.Get get) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitSet(Expression.Set set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String visitThis(Expression.This this_) {
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