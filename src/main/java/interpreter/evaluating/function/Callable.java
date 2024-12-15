package interpreter.evaluating.function;

import java.util.List;

import interpreter.evaluating.Interpreter;
import interpreter.evaluating.Value;

public interface Callable {

	String name();

	int arity();

	Value call(Interpreter interpreter, List<Value> arguments);

	String format();

}