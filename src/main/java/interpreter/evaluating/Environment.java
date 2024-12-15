package interpreter.evaluating;

import java.util.HashMap;
import java.util.Map;

import interpreter.grammar.Token;

public class Environment {

	private final Map<String, Value> values = new HashMap<>();

	public void define(String name, Value value) {
		values.put(name, value);
	}

	public Value get(Token name) {
		final var lexeme = name.lexeme();

		final var value = values.get(lexeme);
		if (value == null) {
			throw new RuntimeError("Undefined variable '%s'.".formatted(lexeme), name);
		}

		return value;
	}

}