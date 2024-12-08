package interpreter.evaluating;

import interpreter.Lox;
import interpreter.grammar.Literal;
import interpreter.parser.Expression;
import lombok.NonNull;

public class Interpreter implements Expression.Visitor<Literal> {

	private final Lox lox;

	public Interpreter(
		@NonNull Lox lox
	) {
		this.lox = lox;
	}

	public Literal evaluate(Expression expression) {
		return visit(expression);
	}

	@Override
	public Literal visitLiteral(Expression.Literal literal) {
		return literal.value();
	}

	@Override
	public Literal visitGrouping(Expression.Grouping grouping) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Literal visitUnary(Expression.Unary unary) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Literal visitBinary(Expression.Binary binary) {
		throw new UnsupportedOperationException();
	}

}