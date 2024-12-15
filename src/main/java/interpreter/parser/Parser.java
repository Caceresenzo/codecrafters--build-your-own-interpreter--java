package interpreter.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import interpreter.Lox;
import interpreter.grammar.Literal;
import interpreter.grammar.Token;
import interpreter.grammar.TokenType;
import lombok.NonNull;

public class Parser {

	private final Lox lox;
	private final List<Token> tokens;

	private int current = 0;

	public Parser(
		@NonNull Lox lox,
		@NonNull List<Token> tokens
	) {
		this.lox = lox;
		this.tokens = tokens;
	}

	public List<Statement> parse() {
		try {
			final var statements = new ArrayList<Statement>();

			while (!isAtEnd()) {
				statements.add(statement());
			}

			return statements;
		} catch (ParseException __) {
			return Collections.emptyList();
		}
	}

	public Optional<Expression> parseExpression() {
		try {
			return Optional.of(expression());
		} catch (ParseException __) {
			return Optional.empty();
		}
	}

	private @NonNull Statement statement() {
		if (match(TokenType.PRINT)) {
			return printStatement();
		}

		return expressionStatement();
	}

	private Statement.Print printStatement() {
		final var value = expression();

		consume(TokenType.SEMICOLON, "Expect ';' after value.");

		return new Statement.Print(value);
	}

	private Statement.Expression expressionStatement() {
		final var expression = expression();

		consume(TokenType.SEMICOLON, "Expect ';' after expression.");

		return new Statement.Expression(expression);
	}

	private @NonNull Expression expression() {
		return equalityExpression();
	}

	private Expression equalityExpression() {
		var expression = comparisonExpression();

		while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			final var operator = previous();
			final var right = comparisonExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression comparisonExpression() {
		var expression = termExpression();

		while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
			final var operator = previous();
			final var right = termExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression termExpression() {
		var expression = factorExpression();

		while (match(TokenType.MINUS, TokenType.PLUS)) {
			final var operator = previous();
			final var right = factorExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression factorExpression() {
		var expression = unaryExpression();

		while (match(TokenType.SLASH, TokenType.STAR)) {
			final var operator = previous();
			final var right = unaryExpression();

			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression unaryExpression() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			final var operator = previous();
			final var right = unaryExpression();

			return new Expression.Unary(operator, right);
		}

		return primaryExpression();
	}

	private Expression primaryExpression() {
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

		throw error(peek(), "Expect expression.");
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}

		return false;
	}

	private boolean match(TokenType... types) {
		for (final var type : types) {
			if (match(type)) {
				return true;
			}
		}

		return false;
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
		if (TokenType.EOF.equals(token.type())) {
			lox.report(token.line(), " at end", message);
		} else {
			lox.report(token.line(), " at '%s'".formatted(token.lexeme()), message);
		}

		throw new ParseException(token, message);
	}

}