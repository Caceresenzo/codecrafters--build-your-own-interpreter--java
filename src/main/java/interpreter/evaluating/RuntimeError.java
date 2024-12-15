package interpreter.evaluating;

import interpreter.grammar.Token;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {

	private final Token token;

	public RuntimeError(String message, Token token) {
		super(message);

		this.token = token;
	}

	public Token token() {
		return token;
	}

}