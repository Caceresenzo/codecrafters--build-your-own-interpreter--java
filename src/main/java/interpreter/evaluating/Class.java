package interpreter.evaluating;

import java.util.List;

import interpreter.evaluating.function.Callable;

public record Class(
	String name
) implements Callable {

	@Override
	public int arity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Value call(Interpreter interpreter, List<Value> arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String format() {
		return name;
	}

}