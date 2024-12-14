package interpreter.evaluating;

import java.util.function.DoubleBinaryOperator;

import interpreter.Lox;
import interpreter.parser.Expression;
import interpreter.util.DoubleOperators;
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
		final var right = evaluate(unary.right());

		return switch (unary.operator().type()) {
			case BANG -> new Value.Boolean(!isTruthy(right));
			case MINUS -> {
				if (right instanceof Value.Number(final var value)) {
					yield new Value.Number(-value);
				}

				throw new UnsupportedOperationException();
			}
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public Value visitBinary(Expression.Binary binary) {
		final var left = evaluate(binary.left());
		final var right = evaluate(binary.right());

		return switch (binary.operator().type()) {
			case MINUS -> applyNumberOperator(left, right, DoubleOperators::substract);
			case PLUS -> {
				if (left instanceof Value.Number(final var leftValue) && right instanceof Value.Number(final var rightValue)) {
					yield new Value.Number(leftValue + rightValue);
				}

				if (left instanceof Value.String(final var leftValue) && right instanceof Value.String(final var rightValue)) {
					yield new Value.String(leftValue + rightValue);
				}

				throw new UnsupportedOperationException();
			}
			case SLASH -> applyNumberOperator(left, right, DoubleOperators::divide);
			case STAR -> applyNumberOperator(left, right, DoubleOperators::multiply);
			default -> throw new UnsupportedOperationException();
		};
	}

	public boolean isTruthy(Value value) {
		return switch (value) {
			case Value.Nil __ -> false;
			case Value.Boolean(final var rawValue) -> rawValue;
			default -> true;
		};
	}

	private Value.Number applyNumberOperator(Value left, Value right, DoubleBinaryOperator operator) {
		if (left instanceof Value.Number(final var leftValue) && right instanceof Value.Number(final var rightValue)) {
			return new Value.Number(operator.applyAsDouble(leftValue, rightValue));
		}

		throw new UnsupportedOperationException();
	}

}