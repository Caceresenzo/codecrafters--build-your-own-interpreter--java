package interpreter.evaluating.value;

import java.util.HashMap;
import java.util.Map;

import interpreter.evaluating.RuntimeError;
import interpreter.grammar.Token;

public final class LoxInstance implements LoxValue {

	private final LoxClass klass;
	private final Map<String, LoxValue> fields = new HashMap<>();

	public LoxInstance(LoxClass klass) {
		this.klass = klass;
	}

	public LoxValue get(Token name) {
		final var value = fields.get(name.lexeme());
		if (value != null) {
			return value;
		}

		final var method = klass.findMethod(name.lexeme());
		if (method != null) {
			return method.bind(this);
		}

		throw new RuntimeError("Undefined property '%s'.".formatted(name.lexeme()), name);
	}

	public void set(Token name, LoxValue value) {
		fields.put(name.lexeme(), value);
	}

	@Override
	public String format() {
		return "%s instance".formatted(klass.name());
	}

}