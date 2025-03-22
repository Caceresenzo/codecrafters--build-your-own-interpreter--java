package interpreter.parser;

import java.util.List;

import interpreter.grammar.Token;
import lombok.NonNull;

public sealed interface Expression {

	public record Literal(
		@NonNull interpreter.grammar.Literal value
	) implements Expression {}

	public record Grouping(
		@NonNull Expression expression
	) implements Expression {}

	public record Unary(
		@NonNull Token operator,
		@NonNull Expression right
	) implements Expression {}

	public record Binary(
		@NonNull Expression left,
		@NonNull Token operator,
		@NonNull Expression right
	) implements Expression {}

	public record Variable(
		@NonNull Token name
	) implements Expression {}

	public record Assign(
		@NonNull Token name,
		@NonNull Expression value
	) implements Expression {}

	public record Logical(
		@NonNull Expression left,
		@NonNull Token operator,
		@NonNull Expression right
	) implements Expression {}

	public record Call(
		@NonNull Expression callee,
		@NonNull Token parenthesis,
		@NonNull List<Expression> arguments
	) implements Expression {}

	public record Get(
		@NonNull Expression object,
		@NonNull Token name
	) implements Expression {}

	public record Set(
		@NonNull Expression object,
		@NonNull Token name,
		@NonNull Expression value
	) implements Expression {}

	public record This(
		@NonNull Token keyword
	) implements Expression {}

	public interface Visitor<T> {

		T visitLiteral(Literal literal);

		T visitGrouping(Grouping grouping);

		T visitUnary(Unary unary);

		T visitBinary(Binary binary);

		T visitVariable(Variable variable);

		T visitAssign(Assign assign);

		T visitLogical(Logical logical);

		T visitCall(Call call);

		T visitGet(Get get);

		T visitSet(Set set);

		T visitThis(This this_);

		default T visit(Expression expression) {
			return switch (expression) {
				case Literal literal -> visitLiteral(literal);
				case Grouping grouping -> visitGrouping(grouping);
				case Unary unary -> visitUnary(unary);
				case Binary binary -> visitBinary(binary);
				case Variable variable -> visitVariable(variable);
				case Assign assign -> visitAssign(assign);
				case Logical logical -> visitLogical(logical);
				case Call call -> visitCall(call);
				case Get get -> visitGet(get);
				case Set set -> visitSet(set);
				case This this_ -> visitThis(this_);
			};
		}

	}

}