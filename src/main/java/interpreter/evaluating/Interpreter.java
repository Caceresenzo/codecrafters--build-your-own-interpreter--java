package interpreter.evaluating;

import java.util.function.DoubleBinaryOperator;

import interpreter.Lox;
import interpreter.grammar.Token;
import interpreter.parser.Expression;
import interpreter.util.DoubleOperators;
import interpreter.util.function.DoubleComparisonOperator;
import lombok.NonNull;

public class Interpreter implements Expression.Visitor<Value> {

	private final Lox lox;

	public Interpreter(
		@NonNull Lox lox
	) {
		this.lox = lox;
	}

	public void interpret(Expression expression) {
		try {
			final var value = evaluate(expression);
			System.out.println(value.format());
		} catch (RuntimeError error) {
			lox.reportRuntime(error.token().line(), error.getMessage());
		}
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

				throw new RuntimeError("Operand must be a number.", unary.operator());
			}
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public Value visitBinary(Expression.Binary binary) {
		final var left = evaluate(binary.left());
		final var right = evaluate(binary.right());

		final var operatorToken = binary.operator();
		return switch (operatorToken.type()) {
			case MINUS -> applyNumberOperator(left, operatorToken, right, DoubleOperators::substract);
			case PLUS -> {
				if (left instanceof Value.Number(final var leftValue) && right instanceof Value.Number(final var rightValue)) {
					yield new Value.Number(leftValue + rightValue);
				}

				if (left instanceof Value.String(final var leftValue) && right instanceof Value.String(final var rightValue)) {
					yield new Value.String(leftValue + rightValue);
				}

				throw new RuntimeError("Operands must be two numbers or two strings.", operatorToken);
			}
			case SLASH -> applyNumberOperator(left, operatorToken, right, DoubleOperators::divide);
			case STAR -> applyNumberOperator(left, operatorToken, right, DoubleOperators::multiply);
			case GREATER -> applyNumberOperator(left, right, DoubleOperators::greaterThan);
			case GREATER_EQUAL -> applyNumberOperator(left, right, DoubleOperators::greaterThanOrEqual);
			case LESS -> applyNumberOperator(left, right, DoubleOperators::lessThan);
			case LESS_EQUAL -> applyNumberOperator(left, right, DoubleOperators::lessThanOrEqual);
			case BANG_EQUAL -> new Value.Boolean(!left.equals(right));
			case EQUAL_EQUAL -> new Value.Boolean(left.equals(right));
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

	private Value.Number applyNumberOperator(Value left, Token token, Value right, DoubleBinaryOperator operator) {
		if (left instanceof Value.Number(final var leftValue) && right instanceof Value.Number(final var rightValue)) {
			return new Value.Number(operator.applyAsDouble(leftValue, rightValue));
		}

		throw new RuntimeError("Operands must be numbers.", token);
	}

	private Value.Boolean applyNumberOperator(Value left, Value right, DoubleComparisonOperator operator) {
		if (left instanceof Value.Number(final var leftValue) && right instanceof Value.Number(final var rightValue)) {
			return new Value.Boolean(operator.applyAsDouble(leftValue, rightValue));
		}

		throw new UnsupportedOperationException();
	}

}