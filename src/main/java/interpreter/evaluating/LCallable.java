package interpreter.evaluating;

import java.util.List;

public non-sealed interface LCallable extends Value {

	String name();

	int arity();

	Value call(Interpreter interpreter, List<Value> arguments);

	String format();

}