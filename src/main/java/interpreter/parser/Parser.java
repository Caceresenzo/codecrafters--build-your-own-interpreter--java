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
		return unary();
	}

	private Expression unary() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			final var operator = previous();
			final var right = unary();

			return new Expression.Unary(operator, right);
		}

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

		if (match(TokenType.STRING, TokenType.NUMBER)) {
			return new Expression.Literal(previous().literal());
		}

		if (match(TokenType.LEFT_PAREN)) {
			final var expression = expression();

			consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");

			return new Expression.Grouping(expression);
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

	private boolean match(TokenType type1, TokenType type2) {
		return match(type1) || match(type2);
	}

	private Token consume(TokenType type, String message) {
		if (check(type)) {
			return advance();
		}

		throw error(peek(), message);
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

	private ParseException error(Token token, String message) {
		// TODO
		throw new UnsupportedOperationException();
	}

}