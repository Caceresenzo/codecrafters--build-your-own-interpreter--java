package interpreter.evaluating;

import java.util.List;

import interpreter.evaluating.function.Callable;

public record Class(
	String name
) implements Callable {

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public Value call(Interpreter interpreter, List<Value> arguments) {
		final var instance = new Instance(this);

		return instance;
	}

	@Override
	public String format() {
		return name;
	}

}