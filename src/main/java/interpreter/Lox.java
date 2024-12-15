package interpreter;

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

	public boolean hadError() {
		return hadError;
	}
	
	public boolean hadRuntimeError() {
		return hadRuntimeError;
	}

}