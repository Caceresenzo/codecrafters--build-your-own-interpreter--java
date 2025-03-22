package interpreter.evaluating;

import interpreter.evaluating.Value.LBoolean;
import interpreter.evaluating.Value.LNil;
import interpreter.evaluating.Value.LNumber;
import interpreter.evaluating.Value.LString;
import lombok.NonNull;

public sealed interface Value permits LNil, LBoolean, LString, LNumber, LCallable, Instance {

	java.lang.String format();

	public record LNil() implements Value {

		@Override
		public java.lang.String format() {
			return "nil";
		}

	}

	public record LBoolean(
		boolean value
	) implements Value {

		@Override
		public java.lang.String format() {
			return java.lang.Boolean.toString(value);
		}

	}

	public record LString(
		@NonNull java.lang.String value
	) implements Value {

		@Override
		public java.lang.String format() {
			return value;
		}

	}

	public record LNumber(
		double value
	) implements Value {

		@Override
		public java.lang.String format() {
			final var longValue = (long) value;
			if (value == longValue) {
				return Long.toString(longValue);
			}

			return Double.toString(value);
		}

	}

}