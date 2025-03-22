package interpreter.parser;

import java.util.List;
import java.util.Optional;

import interpreter.grammar.Token;
import lombok.NonNull;

public sealed interface Statement {

	public record Expression(
		@NonNull interpreter.parser.Expression expression
	) implements Statement {}

	public record Print(
		@NonNull interpreter.parser.Expression expression
	) implements Statement {}

	public record Variable(
		@NonNull Token name,
		@NonNull Optional<interpreter.parser.Expression> initializer
	) implements Statement {}

	public record Block(
		@NonNull List<Statement> statements
	) implements Statement {}

	public record If(
		@NonNull interpreter.parser.Expression condition,
		@NonNull Statement thenBranch,
		@NonNull Optional<Statement> elseBranch
	) implements Statement {}

	public record While(
		@NonNull interpreter.parser.Expression condition,
		@NonNull Statement body
	) implements Statement {}

	public record Function(
		@NonNull Token name,
		@NonNull List<Token> parameters,
		@NonNull List<Statement> body
	) implements Statement {}

	public record Return(
		@NonNull Token keyword,
		@NonNull Optional<interpreter.parser.Expression> value
	) implements Statement {}

	public record Class(
		@NonNull Token name,
		@NonNull List<Function> methods
	) implements Statement {}

	public interface Visitor<T> {

		T visitExpression(Expression expression);

		T visitPrint(Print print);

		T visitVariable(Variable variable);

		T visitBlock(Block block);

		T visitIf(If if_);

		T visitWhile(While while_);

		T visitFunction(Function function);

		T visitReturn(Return return_);

		T visitClass(Class class_);

		default T visit(Statement statement) {
			return switch (statement) {
				case Expression expression -> visitExpression(expression);
				case Print print -> visitPrint(print);
				case Variable variable -> visitVariable(variable);
				case Block block -> visitBlock(block);
				case If if_ -> visitIf(if_);
				case While while_ -> visitWhile(while_);
				case Function function -> visitFunction(function);
				case Return return_ -> visitReturn(return_);
				case Class class_ -> visitClass(class_);
			};
		}

	}

}