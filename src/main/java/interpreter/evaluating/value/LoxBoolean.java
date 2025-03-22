package interpreter.evaluating.value;

public enum LoxBoolean implements LoxValue {

	TRUE(true),
	FALSE(false);

	private final boolean value;

	private LoxBoolean(boolean value) {
		this.value = value;
	}

	public boolean value() {
		return value;
	}

	@Override
	public String format() {
		return Boolean.toString(value);
	}

	public static LoxBoolean valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

}