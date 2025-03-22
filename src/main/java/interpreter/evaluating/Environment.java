package interpreter.evaluating;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import interpreter.evaluating.value.LoxCallable;
import interpreter.evaluating.value.LoxNil;
import interpreter.evaluating.value.LoxValue;
import interpreter.grammar.Token;
import lombok.NonNull;

public class Environment {

	private final Optional<Environment> enclosing;
	private final Map<String, LoxValue> values = new HashMap<>();

	public Environment() {
		this.enclosing = Optional.empty();
	}

	public Environment(@NonNull Environment enclosing) {
		this.enclosing = Optional.of(enclosing);
	}

	public void defineFunction(LoxCallable callable) {
		define(callable.name(), callable);
	}

	public void define(String name, LoxValue value) {
		if (value == null) {
			value = LoxNil.INSTANCE;
		}

		values.put(name, value);
	}

	public LoxValue get(Token name) {
		final var lexeme = name.lexeme();

		final var value = values.get(lexeme);
		if (value != null) {
			return value;
		}

		if (enclosing.isPresent()) {
			return enclosing.get().get(name);
		}

		throw new RuntimeError("Undefined variable '%s'.".formatted(lexeme), name);
	}

	public LoxValue getAt(int distance, String name) {
		final var value = ancestor(distance).values.get(name);

		return Objects.requireNonNull(value);
	}

	public void assign(Token name, LoxValue value) {
		final var lexeme = name.lexeme();

		final var previous = values.replace(lexeme, value);
		if (previous != null) {
			return;
		}

		if (enclosing.isPresent()) {
			enclosing.get().assign(name, value);
			return;
		}

		throw new RuntimeError("Undefined variable '%s'.".formatted(lexeme), name);
	}

	public void assignAt(int distance, Token name, LoxValue value) {
		ancestor(distance).values.put(name.lexeme(), value);
	}

	public Environment ancestor(int distance) {
		var environment = this;

		for (var index = 0; index < distance; index++) {
			environment = environment.enclosing.get();
		}

		return Objects.requireNonNull(environment);
	}

	public Environment inner() {
		return new Environment(this);
	}

}