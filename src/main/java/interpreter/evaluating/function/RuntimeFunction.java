package interpreter.evaluating.function;

import java.util.List;

import interpreter.evaluating.Interpreter;
import interpreter.evaluating.Value;
import interpreter.parser.Statement;
import interpreter.util.Iterators;

public record RuntimeFunction(
	Statement.Function declaration
) implements Callable {

	@Override
	public String name() {
		return declaration.name().lexeme();
	}

	@Override
	public int arity() {
		return declaration.parameters().size();
	}

	@Override
	public Value call(Interpreter interpreter, List<Value> arguments) {
		final var environment = interpreter.environment().inner();

		Iterators.zip(
			declaration.parameters().iterator(),
			arguments.iterator(),
			(parameter, argument) -> environment.define(parameter.lexeme(), argument)
		);

		interpreter.executeBlock(declaration.body(), environment);
		return null;
	}

	@Override
	public String format() {
		return "<fn %s>".formatted(name());
	}

}