package interpreter.parser;

import interpreter.grammar.Token;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class ParseError extends RuntimeException {

	private final Token token;

	public ParseError(Token token, String message) {
		super(message);

		this.token = token;
	}

}