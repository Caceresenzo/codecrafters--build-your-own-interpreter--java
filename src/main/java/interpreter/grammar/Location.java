package interpreter.grammar;

public record Location(
	int start,
	int end,
	int line
) {}