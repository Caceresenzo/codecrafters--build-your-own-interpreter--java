package interpreter.grammar;

import interpreter.evaluating.Value;
import lombok.NonNull;

public sealed interface Literal {

	java.lang.String format();

	Value toValue();

	public record Nil() implements Literal {

		@Override
		public java.lang.String format() {
			return "null";
		}

		@Override
		public Value toValue() {
			return new Value.Nil();
		}

	}

	public record Boolean(
		boolean value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return java.lang.Boolean.toString(value);
		}

		@Override
		public Value toValue() {
			return new Value.Boolean(value);
		}

	}

	public record String(
		@NonNull java.lang.String value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return value;
		}

		@Override
		public Value toValue() {
			return new Value.String(value);
		}

	}

	public record Number(
		double value
	) implements Literal {

		@Override
		public java.lang.String format() {
			return Double.toString(value);
		}

		@Override
		public Value toValue() {
			return new Value.Number(value);
		}

	}

}