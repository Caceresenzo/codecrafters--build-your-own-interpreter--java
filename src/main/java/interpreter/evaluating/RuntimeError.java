package interpreter.evaluating;

import interpreter.grammar.Token;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {

	private final Token token;

	public RuntimeError(String message, Token token) {
		super(message);

		this.token = token;
	}

	protected RuntimeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

		this.token = null;
	}

	public Token token() {
		return token;
	}

}