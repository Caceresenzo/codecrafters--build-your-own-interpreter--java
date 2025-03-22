package interpreter;

import interpreter.grammar.Token;
import interpreter.grammar.TokenType;

public class Lox {

	private boolean hadError = false;
	private boolean hadRuntimeError = false;

	public void report(int line, String where, String message) {
		hadError = true;

		System.err.println("[line %d] Error%s: %s".formatted(line, where, message));
	}

	public void reportRuntime(int line, String message) {
		hadRuntimeError = true;

		System.err.println("%s%n[line %d]".formatted(message, line));
	}

	public void error(Token token, String message) {
		if (TokenType.EOF.equals(token.type())) {
			report(token.line(), " at end", message);
		} else {
			report(token.line(), " at '%s'".formatted(token.lexeme()), message);
		}
	}

	public boolean hadError() {
		return hadError;
	}

	public boolean hadRuntimeError() {
		return hadRuntimeError;
	}

}