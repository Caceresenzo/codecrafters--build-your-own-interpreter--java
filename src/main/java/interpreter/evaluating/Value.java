package interpreter.evaluating;

import lombok.NonNull;

public sealed interface Value {

	java.lang.String format();

	public record Nil() implements Value {

		@Override
		public java.lang.String format() {
			return "nil";
		}

	}

	public record Boolean(
		boolean value
	) implements Value {

		@Override
		public java.lang.String format() {
			return java.lang.Boolean.toString(value);
		}

	}

	public record String(
		@NonNull java.lang.String value
	) implements Value {

		@Override
		public java.lang.String format() {
			return value;
		}

	}

	public record Number(
		double value
	) implements Value {

		@Override
		public java.lang.String format() {
			return Double.toString(value);
		}

	}

}