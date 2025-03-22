package interpreter.evaluating.value;

public record LoxNumber(
	double value
) implements LoxValue {

	@Override
	public String format() {
		final var longValue = (long) value;
		if (value == longValue) {
			return Long.toString(longValue);
		}

		return Double.toString(value);
	}

}