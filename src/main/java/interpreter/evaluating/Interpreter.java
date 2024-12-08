package interpreter.evaluating;

import interpreter.Lox;
import interpreter.parser.Expression;
import lombok.NonNull;

public class Interpreter implements Expression.Visitor<Value> {

	private final Lox lox;

	public Interpreter(
		@NonNull Lox lox
	) {
		this.lox = lox;
	}

	public Value evaluate(Expression expression) {
		return visit(expression);
	}

	@Override
	public Value visitLiteral(Expression.Literal literal) {
		return literal.value().toValue();
	}

	@Override
	public Value visitGrouping(Expression.Grouping grouping) {
		return evaluate(grouping.expression());
	}

	@Override
	public Value visitUnary(Expression.Unary unary) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value visitBinary(Expression.Binary binary) {
		throw new UnsupportedOperationException();
	}

}