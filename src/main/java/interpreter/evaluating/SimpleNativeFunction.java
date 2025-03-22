package interpreter.evaluating;

import java.util.List;
import java.util.function.Function;

import interpreter.evaluating.value.LoxCallable;
import interpreter.evaluating.value.LoxValue;

public record SimpleNativeFunction(
	String name,
	int arity,
	Function<List<LoxValue>, LoxValue> impl
) implements LoxCallable {

	@Override
	public LoxValue call(Interpreter interpreter, List<LoxValue> arguments) {
		return impl.apply(arguments);
	}

	@Override
	public String format() {
		return "<native fn %s>".formatted(name());
	}

}