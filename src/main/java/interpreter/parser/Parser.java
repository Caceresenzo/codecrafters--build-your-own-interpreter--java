package interpreter.parser;

import java.util.List;

import interpreter.grammar.Literal;
import interpreter.grammar.Token;
import interpreter.grammar.TokenType;

public class Parser {

	private final List<Token> tokens;

	private int current;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;

		this.current = 0;
	}

	public Expression parse() {
		return expression();
	}

	private Expression expression() {
		return primary();
	}

	private Expression primary() {
		if (match(TokenType.FALSE)) {
			return new Expression.Literal(new Literal.Boolean(false));
		}

		if (match(TokenType.TRUE)) {
			return new Expression.Literal(new Literal.Boolean(true));
		}

		if (match(TokenType.NIL)) {
			return new Expression.Literal(new Literal.Nil());
		}

		throw new UnsupportedOperationException();
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}

		return false;
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) {
			return false;
		}

		return type.equals(peek().type());
	}

	private Token advance() {
		if (!isAtEnd()) {
			++current;
		}

		return previous();
	}

	private boolean isAtEnd() {
		return TokenType.EOF.equals(peek().type());
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}

}