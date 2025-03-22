package interpreter.evaluating;

import java.util.List;

import interpreter.parser.Statement;
import interpreter.util.Iterators;

public record Function(
	Statement.Function declaration,
	Environment closure
) implements LCallable {

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
		final var environment = closure.inner();

		Iterators.zip(
			declaration.parameters().iterator(),
			arguments.iterator(),
			(parameter, argument) -> environment.define(parameter.lexeme(), argument)
		);

		try {
			interpreter.executeBlock(declaration.body(), environment);

			return new Value.LNil();
		} catch (Return return_) {
			return return_.value();
		}
	}

	public Function bind(Instance instance) {
		final var environment = closure.inner();
		environment.define("this", instance);

		return new Function(declaration, environment);
	}

	@Override
	public String format() {
		return "<fn %s>".formatted(name());
	}

}