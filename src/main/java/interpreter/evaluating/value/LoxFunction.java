package interpreter.evaluating.value;

import java.util.List;

import interpreter.evaluating.Environment;
import interpreter.evaluating.Interpreter;
import interpreter.evaluating.Return;
import interpreter.parser.Statement;
import interpreter.util.Iterators;

public record LoxFunction(
	Statement.Function declaration,
	Environment closure
) implements LoxCallable {

	@Override
	public String name() {
		return declaration.name().lexeme();
	}

	@Override
	public int arity() {
		return declaration.parameters().size();
	}

	@Override
	public LoxValue call(Interpreter interpreter, List<LoxValue> arguments) {
		final var environment = closure.inner();

		Iterators.zip(
			declaration.parameters().iterator(),
			arguments.iterator(),
			(parameter, argument) -> environment.define(parameter.lexeme(), argument)
		);

		try {
			interpreter.executeBlock(declaration.body(), environment);

			return LoxNil.INSTANCE;
		} catch (Return return_) {
			return return_.value();
		}
	}

	public LoxFunction bind(Instance instance) {
		final var environment = closure.inner();
		environment.define("this", instance);

		return new LoxFunction(declaration, environment);
	}

	@Override
	public String format() {
		return "<fn %s>".formatted(name());
	}

}