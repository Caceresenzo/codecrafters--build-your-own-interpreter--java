package interpreter;

public class Lox {

	private boolean hadError = false;

	public void report(int line, String where, String message) {
		hadError = true;

		System.err.println("[line %d] Error%s: %s".formatted(line, where, message));
	}

	public boolean hadError() {
		return hadError;
	}

}