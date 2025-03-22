package interpreter.evaluating;

import java.util.List;
import java.util.Map;

public record LClass(
	String name,
	Map<String, Function> methods
) implements LCallable {

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

	public Function findMethod(String name) {
		return methods.get(name);
	}

}