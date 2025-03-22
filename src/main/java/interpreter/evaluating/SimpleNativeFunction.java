package interpreter.evaluating;

import java.util.List;
import java.util.function.Function;

public record SimpleNativeFunction(
	String name,
	int arity,
	Function<List<Value>, Value> impl
) implements LCallable {

	@Override
	public Value call(Interpreter interpreter, List<Value> arguments) {
		return impl.apply(arguments);
	}

	@Override
	public String format() {
		return "<native fn %s>".formatted(name());
	}

}