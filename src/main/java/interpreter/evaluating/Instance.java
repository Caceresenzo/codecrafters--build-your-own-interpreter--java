package interpreter.evaluating;

import java.util.HashMap;
import java.util.Map;

import interpreter.grammar.Token;

public final class Instance implements Value {

	private final Class klass;
	private final Map<java.lang.String, Value> fields = new HashMap<>();

	public Instance(Class klass) {
		this.klass = klass;
	}

	public Value get(Token name) {
		final var value = fields.get(name.lexeme());
		if (value != null) {
			return value;
		}

		throw new RuntimeError("Undefined property '%s'.".formatted(name.lexeme()), name);
	}

	public void set(Token name, Value value) {
		fields.put(name.lexeme(), value);
	}

	@Override
	public java.lang.String format() {
		return "%s instance".formatted(klass.name());
	}

}