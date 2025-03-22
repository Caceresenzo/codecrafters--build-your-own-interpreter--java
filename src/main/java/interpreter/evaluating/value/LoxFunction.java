package interpreter.evaluating.value;

import java.util.List;

import interpreter.evaluating.Environment;
import interpreter.evaluating.Interpreter;
import interpreter.evaluating.Return;
import interpreter.parser.Statement;
import interpreter.util.Iterators;

public record LoxFunction(
	Statement.Function declaration,
	Environment closure,
	boolean isInitializer
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

		LoxValue returnedValue = LoxNil.INSTANCE;

		try {
			interpreter.executeBlock(declaration.body(), environment);
		} catch (Return return_) {
			returnedValue = return_.value();
		}

		if (isInitializer) {
			return closure.getAt(0, "this");
		}

		return returnedValue;
	}

	public LoxFunction bind(LoxInstance instance) {
		final var environment = closure.inner();
		environment.define("this", instance);

		return new LoxFunction(declaration, environment, isInitializer);
	}

	@Override
	public String format() {
		return "<fn %s>".formatted(name());
	}

}