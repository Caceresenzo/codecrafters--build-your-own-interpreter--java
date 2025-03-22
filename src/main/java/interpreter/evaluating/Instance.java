package interpreter.evaluating;

public record Instance(
	Class klass
) implements Value {

	@Override
	public java.lang.String format() {
		return "%s instance".formatted(klass.name());
	}

}