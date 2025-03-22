package interpreter.evaluating.value;

import java.util.List;

import interpreter.evaluating.Interpreter;

public non-sealed interface LoxCallable extends LoxValue {

	String name();

	int arity();

	LoxValue call(Interpreter interpreter, List<LoxValue> arguments);

	String format();

}