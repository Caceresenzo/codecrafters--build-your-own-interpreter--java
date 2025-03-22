package interpreter.grammar;

import interpreter.evaluating.value.LoxBoolean;
import interpreter.evaluating.value.LoxNil;
import interpreter.evaluating.value.LoxNumber;
import interpreter.evaluating.value.LoxString;
import interpreter.evaluating.value.LoxValue;
import lombok.NonNull;

public sealed interface Literal {

	java.lang.String format();

	LoxValue toValue();

	public record Nil() implements Literal {

		@Override
		public java.lang.String format() {
			return "null";
		}

		@Override
		public LoxValue toValue() {
			return LoxNil.INSTANCE;
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
		public LoxValue toValue() {
			return LoxBoolean.valueOf(value);
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
		public LoxValue toValue() {
			return new LoxString(value);
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
		public LoxValue toValue() {
			return new LoxNumber(value);
		}

	}

}