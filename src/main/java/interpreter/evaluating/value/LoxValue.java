package interpreter.evaluating.value;

public sealed interface LoxValue permits LoxNil, LoxBoolean, LoxString, LoxNumber, LoxCallable, Instance {

	String format();

}