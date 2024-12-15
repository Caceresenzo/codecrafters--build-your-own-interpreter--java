package interpreter.evaluating.function;

import interpreter.evaluating.RuntimeError;
import interpreter.evaluating.Value;

@SuppressWarnings("serial")
public class Return extends RuntimeError {

	private final Value value;

	public Return(Value value) {
		super(null, null, false, false);

		this.value = value;
	}

	public Value value() {
		return value;
	}

}