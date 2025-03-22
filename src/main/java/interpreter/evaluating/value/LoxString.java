package interpreter.evaluating.value;

import lombok.NonNull;

public record LoxString(
	@NonNull String value
) implements LoxValue {

	@Override
	public String format() {
		return value;
	}

}