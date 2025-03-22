package interpreter.evaluating.value;

public enum LoxNil implements LoxValue {

	INSTANCE;

	@Override
	public String format() {
		return "nil";
	}

}