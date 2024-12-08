package interpreter.parser;

import lombok.NonNull;

public sealed interface Literal {

	java.lang.String format();

	public record Nil() implements Literal {

		@Override
		public java.lang.String format() {
			return "null";
		}

	}

	public record Boolean(
		boolean value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return java.lang.Boolean.toString(value);
		}

	}

	public record String(
		@NonNull java.lang.String value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return value;
		}

	}

	public record Number(
		double value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return Double.toString(value);
		}

	}

}