package interpreter.parser;

import interpreter.grammar.Token;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class ParseException extends RuntimeException {

	private final Token token;

	public ParseException(Token token, String message) {
		super(message);

		this.token = token;
	}

}