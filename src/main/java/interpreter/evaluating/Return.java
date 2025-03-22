package interpreter.evaluating;

import interpreter.evaluating.value.LoxValue;

@SuppressWarnings("serial")
public class Return extends RuntimeError {

	private final LoxValue value;

	public Return(LoxValue value) {
		super(null, null, false, false);

		this.value = value;
	}

	public LoxValue value() {
		return value;
	}

}