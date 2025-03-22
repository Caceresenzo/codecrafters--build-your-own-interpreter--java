package interpreter.evaluating.value;

import java.util.List;
import java.util.Map;

import interpreter.evaluating.Interpreter;

public record LoxClass(
	String name,
	Map<String, LoxFunction> methods
) implements LoxCallable {

	@Override
	public int arity() {
		final var initializer = findMethod("init");
		if (initializer != null) {
			return initializer.arity();
		}

		return 0;
	}

	@Override
	public LoxValue call(Interpreter interpreter, List<LoxValue> arguments) {
		final var instance = new LoxInstance(this);

		final var initializer = findMethod("init");
		if (initializer != null) {
			initializer.bind(instance).call(interpreter, arguments);
		}

		return instance;
	}

	@Override
	public String format() {
		return name;
	}

	public LoxFunction findMethod(String name) {
		return methods.get(name);
	}

}