package interpreter.evaluating.function;

import java.util.List;
import java.util.function.Function;

import interpreter.evaluating.Interpreter;
import interpreter.evaluating.Value;

public record SimpleNativeFunction(
	String name,
	int arity,
	Function<List<Value>, Value> impl
) implements Callable {

	@Override
	public Value call(Interpreter interpreter, List<Value> arguments) {
		return impl.apply(arguments);
	}

	@Override
	public String format() {
		return "<native fn %s>".formatted(name());
	}

}