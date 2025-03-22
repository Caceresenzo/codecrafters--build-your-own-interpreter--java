package interpreter.evaluating.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum LoxBoolean implements LoxValue {

	TRUE(true),
	FALSE(false);

	private final boolean value;

	@Override
	public String format() {
		return Boolean.toString(value);
	}

	public static LoxBoolean valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

}